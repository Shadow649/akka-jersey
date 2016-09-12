package com.shad649.actor.messages;

import java.io.Serializable;
import java.util.Date;

/**
 * The transaction are aggregated every second. 
 * @author Emanuele Lombardi
 *
 */
public class ProcessedTransactions implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The date where the transactions were aggregated
     */
    private final Date date;
    /**
     * The number of the transaction processed
     */
    private final int number;
    
    public ProcessedTransactions(Date date, int number) {
        this.date = date;
        this.number = number;
    }
    
    public Date getDate() {
        return date;
    };
    
    public int getNumber() {
        return number;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProcessedTransactions other = (ProcessedTransactions) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        return true;
    }
    
}
