package com.rstejskalprojects.reservationsystem.util.customexception;

public class LocationAlreadyExistsException extends RuntimeException {
    public LocationAlreadyExistsException() {
    }

    public LocationAlreadyExistsException(String message) {
        super(message);
    }
}

