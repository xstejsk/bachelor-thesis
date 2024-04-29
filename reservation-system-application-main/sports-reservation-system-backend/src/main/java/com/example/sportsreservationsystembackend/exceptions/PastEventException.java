package com.example.sportsreservationsystembackend.exceptions;

public class PastEventException extends RuntimeException {
    public PastEventException() {
    }

    public PastEventException(String message) {
        super(message);
    }
}
