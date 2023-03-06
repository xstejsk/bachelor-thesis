package com.rstejskalprojects.reservationsystem.util.customexception;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException() {
    }

    public LocationNotFoundException(String message) {
        super(message);
    }
}
