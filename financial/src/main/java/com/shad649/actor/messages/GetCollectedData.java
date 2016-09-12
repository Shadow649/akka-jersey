package com.shad649.actor.messages;

import java.io.Serializable;
/**
 * Sent from the REST endpoint when a client ask for trends.
 * @author Emanuele Lombardi
 *
 */
public class GetCollectedData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public GetCollectedData() {
    }
    
}
