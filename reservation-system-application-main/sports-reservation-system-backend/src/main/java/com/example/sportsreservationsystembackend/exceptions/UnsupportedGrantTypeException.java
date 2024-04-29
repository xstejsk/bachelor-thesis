package com.example.sportsreservationsystembackend.exceptions;

public class UnsupportedGrantTypeException extends RuntimeException {
    public UnsupportedGrantTypeException() {
    }

    public UnsupportedGrantTypeException(String message) {
        super(message);
    }
}
