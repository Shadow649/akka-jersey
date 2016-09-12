package com.shad649.actor.messages;

import java.io.Serializable;

/**
 * Message used to persist the analysis on transactions
 * @author Emanuele Lombardi
 *
 */
public class Store implements Serializable{

    private static final long serialVersionUID = 5961776079242601724L;
    /**
     * The currencies in format CURFROM/CURTO
     */
    private final String transactionCurrencies;
    /**
     * The exchanges between CURFROM/CURTO
     */
    private final ProcessedTransactions numberOfTransaction;
    public Store(String tc, ProcessedTransactions number) {
        this.transactionCurrencies = tc;
        this.numberOfTransaction = number;
    }
    
    public ProcessedTransactions getNumberOfTransaction() {
        return numberOfTransaction;
    }
    
    public String getTransactionCurrencies() {
        return transactionCurrencies;
    }
}
