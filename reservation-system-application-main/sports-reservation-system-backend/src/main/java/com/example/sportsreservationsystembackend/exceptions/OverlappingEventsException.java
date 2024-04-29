package com.example.sportsreservationsystembackend.exceptions;

public class OverlappingEventsException extends RuntimeException {
    public OverlappingEventsException() {
    }

    public OverlappingEventsException(String message) {
        super(message);
    }
}
