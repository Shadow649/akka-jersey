package com.shad649.transaction.generator.actor;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.shad649.transaction.Transaction;
import com.shad649.transaction.generator.SimplePoster;
import com.shad649.transaction.generator.actor.messages.MessageSent;
import com.shad649.transaction.generator.actor.messages.PostMessage;
import com.shad649.transaction.generator.actor.messages.SendMessage;
import com.shad649.transaction.generator.actor.messages.Stop;

public class RequesterActor extends UntypedActor {
    
    private final ObjectMapper mapper = new ObjectMapper();
    private final SimplePoster poster = new SimplePoster();
    private Cancellable cancellable;
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof SendMessage) {
            final SendMessage m = (SendMessage) message;
            final ActorRef sender = getSender();
            if (m.messagesPerMinutes() > 0) {
                long interval = (long) (60000000000l / new Double(
                        m.messagesPerMinutes()));
                log.info("Interval{}: {}", getSelf().path(), interval);
                cancellable = getContext()
                        .system()
                        .scheduler()
                        .schedule(
                                Duration.Zero(),
                                Duration.create(interval, TimeUnit.NANOSECONDS),
                                new Runnable() {
                                    public void run() {
                                        String jsonString;
                                        try {
                                            jsonString = mapper
                                                    .writeValueAsString(new Transaction());
                                            getSelf().tell(
                                                    new PostMessage(sender,
                                                            jsonString),
                                                    getSelf());
                                        } catch (JsonProcessingException e) {
                                            throw new RuntimeJsonMappingException(
                                                    e.getMessage());
                                        }
                                    }
                                }, getContext().system().dispatcher());
                getContext().become(new Procedure<Object>() {
                    
                    public void apply(Object param) throws Exception {
                        if (param instanceof Stop) {
                            cancellable.cancel();
                            getContext().unbecome();
                        } else if (param instanceof PostMessage){
                            PostMessage message = (PostMessage) param;
                            poster.doPost(message.getMessage());
                            log.debug(
                                    "worker {}: message sent: {}",
                                    getSelf().path(),
                                    message.getMessage());
                            
                            message.getSender().tell(new MessageSent(),
                                    getSelf());
                        } else {
                            unhandled(param);
                        }
                    }
                });
            }
        } else {
            unhandled(message);
        }
        
    }
}
