package com.rstejskalprojects.reservationsystem.util.customexception;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException() {
    }

    public ExpiredTokenException(String message) {
        super(message);
    }
}
