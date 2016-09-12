package com.shad649.transaction.generator.actor.messages;

import java.io.Serializable;

import akka.actor.ActorRef;

public class PostMessage implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final ActorRef sender;
    private final String message;
    
    public PostMessage(ActorRef sender, String message) {
        this.sender = sender;
        this.message = message;
    }
    
    public ActorRef getSender() {
        return sender;
    }
    
    public String getMessage() {
        return message;
    }
}
