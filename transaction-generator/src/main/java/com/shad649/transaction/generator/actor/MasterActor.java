package com.shad649.transaction.generator.actor;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.shad649.transaction.generator.PostException;
import com.shad649.transaction.generator.actor.messages.MessageSent;
import com.shad649.transaction.generator.actor.messages.SendMessage;
import com.shad649.transaction.generator.actor.messages.Stop;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.actor.SupervisorStrategy.Directive;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import akka.japi.Procedure;

import static akka.actor.SupervisorStrategy.resume;
import static akka.actor.SupervisorStrategy.escalate;;


public class MasterActor extends UntypedActor {
    
    private final static int CORES = Runtime.getRuntime().availableProcessors() * 4;
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private int messagesSent;
    private static SupervisorStrategy strategy = new OneForOneStrategy(1,
            Duration.create(1, TimeUnit.SECONDS),
            new Function<Throwable, SupervisorStrategy.Directive>() {
                
                @Override
                public Directive apply(Throwable t) throws Exception {
                    if(t instanceof PostException) {
                        return resume();
                    } else {
                        return escalate();
                    }
                }
            });
    
    
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    };
    
    @Override
    public void preStart() throws Exception {
        log.debug("Start creating child workers");
        for (int i = 0; i < CORES; i++) {
            getContext().actorOf(Props.create(RequesterActor.class),
                    "requester" + i);
        }
        log.debug("workers created");
        
        super.preStart();
    }
    
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof SendMessage) {
            int messages = ((SendMessage) message).messagesPerMinutes();
            log.debug("received SendMessage with {} messages per minute",
                    ((SendMessage) message).messagesPerMinutes());
            
            int result = messages / CORES;
            log.info("messages: {}", result);
            int remainder = messages % CORES;
            log.info("remainder: {}", remainder);
            
            Iterator<ActorRef> it = getContext().getChildren().iterator();
            for (int i = 0; i < remainder; i++) {
                ActorRef child = it.next();
                child.tell(new SendMessage(result + 1), getSelf());
                /*
                 * getContext() .system() .scheduler()
                 * .scheduleOnce(Duration.create(500l * i,
                 * TimeUnit.MILLISECONDS), child, new SendMessage(result + 1),
                 * getContext().system().dispatcher(), getSelf()); } int
                 * multiplier = CORES - remainder;
                 */
            }
            while (it.hasNext()) {
                it.next().tell(new SendMessage(result), getSelf());
                /*
                 * getContext() .system() .scheduler()
                 * .scheduleOnce(Duration.create(500l * multiplier,
                 * TimeUnit.MILLISECONDS), it.next(), new SendMessage(result),
                 * getContext().system().dispatcher(), getSelf()); multiplier++;
                 * }
                 */
            }
            getContext().become(started);
        } else if (message instanceof MessageSent) {
            getSender().tell(messagesSent, getSelf());
        } else {
            unhandled(message);
        }
    }
    
    Procedure<Object> started = new Procedure<Object>() {
        
        public void apply(Object param) throws Exception {
            if (param instanceof Stop) {
                Iterable<ActorRef> collection = getContext().getChildren();
                for (ActorRef actorRef : collection) {
                    actorRef.tell(new Stop(), getSelf());
                }
                getContext().unbecome();
            } else if (param instanceof MessageSent) {
                messagesSent++;
            } else {
                unhandled(param);
            }
        }
    };
    
}
