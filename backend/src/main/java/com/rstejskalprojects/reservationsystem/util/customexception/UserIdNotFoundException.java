package com.rstejskalprojects.reservationsystem.util.customexception;

public class UserIdNotFoundException extends RuntimeException {
    public UserIdNotFoundException(String message) {
        super(message);
    }
}
