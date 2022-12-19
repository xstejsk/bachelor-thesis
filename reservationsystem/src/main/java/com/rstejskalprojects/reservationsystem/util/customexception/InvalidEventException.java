package com.rstejskalprojects.reservationsystem.util.customexception;

public class InvalidEventException extends RuntimeException {
    public InvalidEventException() {
    }

    public InvalidEventException(String message) {
        super(message);
    }
}
