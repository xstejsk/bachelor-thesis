package com.example.sportsreservationsystembackend.service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This enum represents email body placeholders
 *
 * @author Radim Stejskal
 */
@RequiredArgsConstructor
@Getter
public enum EmailBodyPlaceholders {
    RECIPIENT("{recipient}"),
    LINK("{link}"),
    LINK_EXPIRATION("{linkExpiration}"),
    EVENT_NAME("{eventName}"),
    PASSWORD("{password}"),
    EVENT_TIME("{eventTime}"),
    EVENT_DATE("{eventDate}"),
    LOCATION_NAME("{locationName}");

    private final String value;


}
