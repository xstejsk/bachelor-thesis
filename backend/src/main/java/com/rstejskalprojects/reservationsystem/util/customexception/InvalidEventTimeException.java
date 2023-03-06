package com.rstejskalprojects.reservationsystem.util.customexception;

public class InvalidEventTimeException extends RuntimeException {
    public InvalidEventTimeException() {
    }

    public InvalidEventTimeException(String message) {
        super(message);
    }
}
