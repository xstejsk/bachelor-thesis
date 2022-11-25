package com.rstejskalprojects.reservationsystem.util.customexception;

public class ExpiredRegistrationTokenException extends RuntimeException {
    public ExpiredRegistrationTokenException() {
    }

    public ExpiredRegistrationTokenException(String message) {
        super(message);
    }
}
