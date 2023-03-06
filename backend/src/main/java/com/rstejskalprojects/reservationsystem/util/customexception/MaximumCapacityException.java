package com.rstejskalprojects.reservationsystem.util.customexception;

public class MaximumCapacityException extends RuntimeException {
    public MaximumCapacityException(String message) {
        super(message);
    }
}
