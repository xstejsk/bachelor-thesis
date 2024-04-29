package com.example.sportsreservationsystembackend.exceptions;

public class DuplicateReservationException extends RuntimeException {
    public DuplicateReservationException() {
        super();
    }

    public DuplicateReservationException(String message) {
        super(message);
    }
}
