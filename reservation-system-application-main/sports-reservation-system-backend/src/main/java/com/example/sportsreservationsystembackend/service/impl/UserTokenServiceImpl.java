package com.example.sportsreservationsystembackend.service.impl;

import com.example.sportsreservationsystembackend.exceptions.InvalidTokenException;
import com.example.sportsreservationsystembackend.exceptions.ResourceNotFoundException;
import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.TokenType;
import com.example.sportsreservationsystembackend.model.UserToken;
import com.example.sportsreservationsystembackend.repository.UserTokenRepository;
import com.example.sportsreservationsystembackend.service.UserTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * This class represents user token service implementation
 *
 * @author Radim Stejskal
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserTokenServiceImpl implements UserTokenService {

    private final UserTokenRepository userTokenRepository;

    /**
     * This method returns user token by token
     * @param token to be found
     * @return found user token
     */
    @Override
    public UserToken getByToken(String token) {
        return userTokenRepository.findByToken(token).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Token %s not found", token)));
    }

    /**
     * This method saves user token to database
     * @param userToken to be saved
     * @return saved user token
     */
    @Override
    public UserToken save(UserToken userToken) {
        return userTokenRepository.save(userToken);
    }

    /**
     * This method verifies user token
     * @param token to be verified
     * @return verified user
     */
    @Override
    public AppUser verifyToken(String token) {
        UserToken userToken = getByToken(token);
        if (userToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token expired");
        }
        if (userToken.getConfirmedAt() != null) {

            throw new InvalidTokenException("Token already verified");
        }
        if (userToken.getTokenType() != TokenType.REGISTRATION) {
            throw new InvalidTokenException("Invalid token type");
        }
        userToken.setConfirmedAt(LocalDateTime.now());
        save(userToken);
        return userToken.getUser();
    }
}
