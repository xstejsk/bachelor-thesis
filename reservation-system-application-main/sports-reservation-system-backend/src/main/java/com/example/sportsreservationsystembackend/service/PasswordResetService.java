package com.example.sportsreservationsystembackend.service;

public interface PasswordResetService {

    void submitPasswordChange(String token);

    void resetPassword(String email);
}

