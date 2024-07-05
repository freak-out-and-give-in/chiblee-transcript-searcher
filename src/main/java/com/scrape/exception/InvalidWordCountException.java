package com.scrape.exception;

public class InvalidWordCountException extends RuntimeException {

    public InvalidWordCountException(String message) {
        super(message);
    }
}
