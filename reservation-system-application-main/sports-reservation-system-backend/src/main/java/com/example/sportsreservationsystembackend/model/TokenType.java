package com.example.sportsreservationsystembackend.model;

/**
 * This enum represents token type
 * @author Radim Stejskal
 */

public enum TokenType {
    REGISTRATION("REGISTRATION"),
    PASSWORD_RESET("PASSWORD_RESET");

    private final String name;

    TokenType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
