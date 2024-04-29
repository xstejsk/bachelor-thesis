package com.example.sportsreservationsystembackend.exceptions;

public class ResourceNotOwnedException extends RuntimeException {

    public ResourceNotOwnedException(String message) {
        super(message);
    }
}
