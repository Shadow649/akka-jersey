package com.shad649.actor.messages;

import java.io.Serializable;

/**
 * Wraps the transaction to process.
 * @author Emanuele Lombardi
 *
 */
public class ProcessTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String transaction;
    public ProcessTransaction(String t) {
        transaction = t;
    }
    
    public String getTransaction() {
        return transaction;
    }
}
