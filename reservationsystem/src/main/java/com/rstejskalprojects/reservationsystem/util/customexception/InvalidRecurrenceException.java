package com.rstejskalprojects.reservationsystem.util.customexception;

public class InvalidRecurrenceException extends RuntimeException {
    public InvalidRecurrenceException() {
    }

    public InvalidRecurrenceException(String message) {
        super(message);
    }
}
