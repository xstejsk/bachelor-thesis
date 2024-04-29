package com.example.sportsreservationsystembackend.exceptions;

public class EventHasReservationsException extends RuntimeException {

        public EventHasReservationsException(String message) {
            super(message);
        }
}
