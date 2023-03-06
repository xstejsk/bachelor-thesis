package com.rstejskalprojects.reservationsystem.util.customexception;

public class AlreadyRegisteredException extends RuntimeException {
    public AlreadyRegisteredException(String message) {
        super(message);
    }
}
