package com.example.sportsreservationsystembackend.service.impl;

import com.example.sportsreservationsystembackend.exceptions.ResourceNotFoundException;
import com.example.sportsreservationsystembackend.model.RefreshToken;
import com.example.sportsreservationsystembackend.repository.RefreshTokenRepository;
import com.example.sportsreservationsystembackend.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * This class represents refresh token service implementation
 *
 * @author Radim Stejskal
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * This method saves refresh token to database
     * @param refreshToken to be saved
     * @return saved refresh token
     */
    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token cannot be null");
        }
        if (refreshToken.getToken() == null) {
            throw new IllegalArgumentException("Refresh token token cannot be null");
        }
        if (refreshToken.getToken().isEmpty()) {
            throw new IllegalArgumentException("Refresh token token cannot be empty");
        }
        if (refreshToken.getUser() == null) {
            throw new IllegalArgumentException("Refresh token user cannot be null");
        }
        log.info("Saving refresh token {}", refreshToken);
        return refreshTokenRepository.save(refreshToken);
    }


    /**
     * This method returns refresh token by token
     * @param token to be found
     * @return found refresh token
     */
    @Override
    public RefreshToken findByToken(String token) {
        log.info("Finding refresh token by token {}", token);
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
    }
}
