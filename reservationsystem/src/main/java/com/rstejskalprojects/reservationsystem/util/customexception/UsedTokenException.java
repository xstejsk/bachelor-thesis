package com.rstejskalprojects.reservationsystem.util.customexception;

public class UsedTokenException extends RuntimeException {
    public UsedTokenException() {
    }

    public UsedTokenException(String message) {
        super(message);
    }
}
