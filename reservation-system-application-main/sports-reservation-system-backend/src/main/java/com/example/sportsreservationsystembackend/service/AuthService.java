package com.example.sportsreservationsystembackend.service;

import com.xstejsk.reservationapp.main.rest.model.AuthenticationResponse;

public interface AuthService {

    AuthenticationResponse authenticate(String grantType, String username, String password, String refreshToken);
}
