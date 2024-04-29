package com.example.sportsreservationsystembackend.exceptions;

public class ApiCallException extends RuntimeException {

    public ApiCallException(String message) {
        super(message);
    }

}
