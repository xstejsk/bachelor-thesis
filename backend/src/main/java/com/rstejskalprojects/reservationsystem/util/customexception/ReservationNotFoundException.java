package com.rstejskalprojects.reservationsystem.util.customexception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException() {
    }

    public ReservationNotFoundException(String message) {
        super(message);
    }
}
