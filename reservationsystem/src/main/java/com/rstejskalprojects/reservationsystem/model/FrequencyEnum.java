package com.rstejskalprojects.reservationsystem.model;

public enum FrequencyEnum {
    NEVER("NEVER"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY");

    private final String value;
    FrequencyEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
