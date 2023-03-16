package com.rstejskalprojects.reservationsystem.model.dto;

public enum EmailBodyPlaceholdersEnum {
    RECIPIENT("{recipient}"),
    LINK("{link}"),
    LINK_EXPIRATION("{linkExpiration}"),
    EVENT_NAME("{eventName}"),
    PASSWORD("{password}"),
    EVENT_TIME("{eventTime}"),
    LOCATION_NAME("{locationName}");


    private final String value;
    EmailBodyPlaceholdersEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


