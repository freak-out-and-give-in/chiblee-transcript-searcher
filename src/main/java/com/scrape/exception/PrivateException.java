package com.scrape.exception;

// This is an exception that encompasses many kinds of errors.
// Its purpose is to be an exception that the user is not told about and the developer is.
public class PrivateException extends RuntimeException {

    public PrivateException(String message) {
        super(message);
    }
}
