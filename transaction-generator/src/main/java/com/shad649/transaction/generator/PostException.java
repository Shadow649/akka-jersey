package com.shad649.transaction.generator;

public class PostException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PostException(Throwable e) {
        super(e);
    }

    public PostException(String string) {
        super(string);
    }
    
}
