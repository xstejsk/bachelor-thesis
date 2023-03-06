package com.rstejskalprojects.reservationsystem.util.customexception;

public class RecurrenceGroupNotFoundException extends RuntimeException {
    public RecurrenceGroupNotFoundException() {
    }

    public RecurrenceGroupNotFoundException(String message) {
        super(message);
    }
}
