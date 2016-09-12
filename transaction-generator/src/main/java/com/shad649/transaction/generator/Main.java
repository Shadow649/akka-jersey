package com.shad649.transaction.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shad649.transaction.generator.actor.MasterActor;
import com.shad649.transaction.generator.actor.messages.MessageSent;
import com.shad649.transaction.generator.actor.messages.SendMessage;
import com.shad649.transaction.generator.actor.messages.Stop;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class Main {
  
  public static void main(String[] args) throws JsonProcessingException,
      InterruptedException {
    final ActorSystem system = ActorSystem.create("system",
        ConfigFactory.load("application"));
    ActorRef masterActorRef = system.actorOf(Props.create(MasterActor.class));
    int messages = 500;
    masterActorRef.tell(new SendMessage(messages), ActorRef.noSender());
    Thread.sleep(60000);
    masterActorRef.tell(new Stop(), ActorRef.noSender());
    Timeout timeout = new Timeout(Duration.create(2, "seconds"));
    
    Future<Object> future = Patterns.ask(masterActorRef, new MessageSent(),
        timeout);
    future.onComplete(new OnComplete<Object>() {
      
      public void onComplete(Throwable failure, Object result) {
        if (failure != null) {
          System.out.println(failure.getMessage());
        } else {
          System.out.println("Messages sent: " + result);
          
        }
        
      }
    }, system.dispatcher());
    system.terminate();
  }
}
