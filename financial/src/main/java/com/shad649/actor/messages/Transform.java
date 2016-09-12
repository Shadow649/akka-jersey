package com.shad649.actor.messages;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Transform implements Serializable {


    private static final long serialVersionUID = 1L;
    private final Map<String, List<ProcessedTransactions>> data;
    
    public Transform(Map<String, List<ProcessedTransactions>> data) {
        this.data = data;
    }

    public Map<String, List<ProcessedTransactions>> getData() {
        return data;
    }
    
}
