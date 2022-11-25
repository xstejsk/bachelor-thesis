package com.rstejskalprojects.reservationsystem.util.customexception;

public class UnknownRegistrationTokenException extends RuntimeException {
    public UnknownRegistrationTokenException() {
    }

    public UnknownRegistrationTokenException(String message) {
        super(message);
    }
}
