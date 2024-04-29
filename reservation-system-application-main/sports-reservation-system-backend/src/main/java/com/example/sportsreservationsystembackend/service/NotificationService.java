package com.example.sportsreservationsystembackend.service;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.Event;

public interface NotificationService {

    void sendRegistrationEmail(AppUser recipient, String link);

    void sendNewReservationEmail(AppUser recipient, Event event);

    void sendReservationCancelledEmail(AppUser recipient, Event event);

    void sendResetPasswordEmail(AppUser recipient, String token, String password);
}
