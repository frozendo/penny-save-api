package com.frozendo.pennysave.exceptions;

public class EmailException extends RuntimeException {
    public EmailException(Exception exception) {
        super("Error on send email!", exception);
    }
}
