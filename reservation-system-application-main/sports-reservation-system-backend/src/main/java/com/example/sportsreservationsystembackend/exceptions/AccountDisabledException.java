package com.example.sportsreservationsystembackend.exceptions;

public class AccountDisabledException extends RuntimeException {

    public AccountDisabledException(String message) {
        super(message);
    }

}
