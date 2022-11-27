package com.rstejskalprojects.reservationsystem.model;

public enum FrequencyEnum {
    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY"),
    YEARLY("YEARLY");

    private final String value;
    FrequencyEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
