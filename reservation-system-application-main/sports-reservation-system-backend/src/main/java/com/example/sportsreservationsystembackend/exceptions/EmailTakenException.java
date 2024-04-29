package com.example.sportsreservationsystembackend.exceptions;

public class EmailTakenException extends RuntimeException {

    public EmailTakenException(String message) {
        super(message);
    }
}
