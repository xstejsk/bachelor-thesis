package com.rstejskalprojects.reservationsystem.model;

public enum TokenTypeEnum {
    REGISTRATION("REGISTRATION"),
    PASSWORD_RESET("PASSWORD_RESET");

    private final String name;

    TokenTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
