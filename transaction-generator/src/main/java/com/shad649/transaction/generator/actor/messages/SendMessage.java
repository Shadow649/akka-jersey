package com.shad649.transaction.generator.actor.messages;

import java.io.Serializable;

public class SendMessage implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = -7085867973870809808L;
  private final int messagesPerMinutes;
  
  public SendMessage(int messagesPerMinutes) {
    this.messagesPerMinutes = messagesPerMinutes;
  }
  
  public int messagesPerMinutes() {
    return messagesPerMinutes;
  }
}
