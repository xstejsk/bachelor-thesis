package com.example.sportsreservationsystembackend.service;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.UserToken;

public interface UserTokenService {

    UserToken getByToken(String token);

    UserToken save(UserToken userToken);

    AppUser verifyToken(String token);
}
