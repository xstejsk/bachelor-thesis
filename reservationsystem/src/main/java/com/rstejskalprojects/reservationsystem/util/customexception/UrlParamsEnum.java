package com.rstejskalprojects.reservationsystem.util.customexception;

public enum UrlParamsEnum {
    LOCATION_ID("locationId"),
    LOCATION_NAME("locationName");

    private final String value;
    UrlParamsEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
