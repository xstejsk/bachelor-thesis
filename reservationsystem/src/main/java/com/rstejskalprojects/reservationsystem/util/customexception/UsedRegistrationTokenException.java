package com.rstejskalprojects.reservationsystem.util.customexception;

public class UsedRegistrationTokenException extends RuntimeException {
    public UsedRegistrationTokenException() {
    }

    public UsedRegistrationTokenException(String message) {
        super(message);
    }
}
