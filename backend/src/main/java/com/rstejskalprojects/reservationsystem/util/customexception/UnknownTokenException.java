package com.rstejskalprojects.reservationsystem.util.customexception;

public class UnknownTokenException extends RuntimeException {
    public UnknownTokenException() {
    }

    public UnknownTokenException(String message) {
        super(message);
    }
}
