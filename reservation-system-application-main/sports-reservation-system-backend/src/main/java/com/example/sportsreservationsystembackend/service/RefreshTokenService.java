package com.example.sportsreservationsystembackend.service;

import com.example.sportsreservationsystembackend.model.RefreshToken;

public interface RefreshTokenService {

    RefreshToken save(RefreshToken refreshToken);

    RefreshToken findByToken(String token);
}
