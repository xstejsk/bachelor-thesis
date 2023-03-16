package com.rstejskalprojects.reservationsystem.service;


public interface EmailFormatterService {

       String formatReservationConfirmationEmail(String recipient, String eventName, String locationName, String eventTime);

       String formatReservationCancellationEmail(String recipient, String eventName, String locationName, String eventTime);

       String formatEventCancellationEmail(String recipient, String eventName, String locationName, String eventTime);

       String formatRegistrationConfirmationEmail(String recipient, String link, Integer linkExpirationMinutes);

       String formatPasswordResetEmail(String recipient, String link, Integer linkExpirationMinutes, String password);

       String formatRecurrentEventCancellationEmail(String recipient, String eventName);
}
