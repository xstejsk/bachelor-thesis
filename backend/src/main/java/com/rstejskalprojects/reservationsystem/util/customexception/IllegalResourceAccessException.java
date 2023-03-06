package com.rstejskalprojects.reservationsystem.util.customexception;

public class IllegalResourceAccessException extends RuntimeException {
    public IllegalResourceAccessException(String message) {
        super(message);
    }
}
