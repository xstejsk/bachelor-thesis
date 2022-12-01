package com.rstejskalprojects.reservationsystem.util.customexception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException() {
    }

    public EventNotFoundException(String message) {
        super(message);
    }
}
