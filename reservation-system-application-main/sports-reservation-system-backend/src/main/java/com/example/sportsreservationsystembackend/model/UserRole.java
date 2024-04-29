package com.example.sportsreservationsystembackend.model;

/**
 * This enum represents user role
 * @author Radim Stejskal
 */

public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String name;
    UserRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

