package com.example.sportsreservationsystembackend.exceptions;

public class EventFullException extends RuntimeException {
    public EventFullException() {
        super();
    }

    public EventFullException(String message) {
        super(message);
    }
}
