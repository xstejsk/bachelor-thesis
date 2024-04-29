package com.example.sportsreservationsystembackend.utils;

import com.example.sportsreservationsystembackend.service.model.EmailBodyPlaceholders;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This class represents email formatter
 *
 * @Author Radim Stejskal
 */
@Getter
public class EmailFormatter {

    private final String link;
    private final String linkExpiration;
    private final String recipientName;
    private final String eventName;
    private final String locationName;
    private final String eventDate;
    private final String eventTime;
    private final String password;

    public EmailFormatter(Builder builder) {
        this.link = builder.link;
        this.linkExpiration = builder.linkExpiration;
        this.recipientName = builder.recipientName;
        this.eventName = builder.eventName;
        this.locationName = builder.locationName;
        this.eventDate = builder.eventDate;
        this.eventTime = builder.eventTime;
        this.password = builder.password;
    }

    @NoArgsConstructor
    public static class Builder {

        private String link = "";
        private String linkExpiration = "";
        private String recipientName = "";
        private String eventName = "";
        private String locationName = "";
        private String eventDate = "";
        private String eventTime = "";
        private String password = "";

        public Builder link(String link) {
            this.link = link;
            return this;
        }

        public Builder linkExpiration(String linkExpiration) {
            this.linkExpiration = linkExpiration;
            return this;
        }

        public Builder recipientName(String recipientName) {
            this.recipientName = recipientName;
            return this;
        }

        public Builder eventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public Builder locationName(String locationName) {
            this.locationName = locationName;
            return this;
        }

        public Builder eventDate(String eventDate) {
            this.eventDate = eventDate;
            return this;
        }

        public Builder eventTime(String eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public EmailFormatter build() {
            return new EmailFormatter(this);
        }
    }

    /**
     * This method replaces placeholders in email body with actual values
     * @param body email body
     * @return formatted email body
     */
    public String formatEmail(String body) {
        body = body.replace(EmailBodyPlaceholders.LINK.getValue(), link);
        body = body.replace(EmailBodyPlaceholders.LINK_EXPIRATION.getValue(), linkExpiration);
        body = body.replace(EmailBodyPlaceholders.RECIPIENT.getValue(), recipientName);
        body = body.replace(EmailBodyPlaceholders.LOCATION_NAME.getValue(), locationName);
        body = body.replace(EmailBodyPlaceholders.EVENT_NAME.getValue(), eventName);
        body = body.replace(EmailBodyPlaceholders.PASSWORD.getValue(), password);
        body = body.replace(EmailBodyPlaceholders.EVENT_TIME.getValue(), eventTime);
        body = body.replace(EmailBodyPlaceholders.EVENT_DATE.getValue(), eventDate);

        return body;
    }
}
