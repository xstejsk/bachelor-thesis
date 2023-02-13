package com.rstejskalprojects.reservationsystem.util.customexception;

public class PastEventException extends RuntimeException {
    public PastEventException(String message) {
        super(message);
    }
}
