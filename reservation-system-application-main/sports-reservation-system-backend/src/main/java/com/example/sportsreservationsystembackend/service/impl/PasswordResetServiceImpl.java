package com.example.sportsreservationsystembackend.service.impl;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.TokenType;
import com.example.sportsreservationsystembackend.model.UserToken;
import com.example.sportsreservationsystembackend.service.NotificationService;
import com.example.sportsreservationsystembackend.service.PasswordResetService;
import com.example.sportsreservationsystembackend.service.UserService;
import com.example.sportsreservationsystembackend.service.UserTokenService;
import com.example.sportsreservationsystembackend.utils.PasswordUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This class represents password reset service implementation
 *
 * @Author Radim Stejskal
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final static int PASSWORD_LENGTH = 8;
    private final UserTokenService userTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserService userService;
    private final NotificationService notificationService;

    /**
     * This method submits password change
     * @param token password reset token
     */
    @Override
    @Transactional
    public void submitPasswordChange(String token) {
        log.info("Password change request for token " + token);
        UserToken userToken = userTokenService.getByToken(token);
        if (userToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }
        if (userToken.getConfirmedAt() != null) {
            throw new RuntimeException("Token already used");
        }
        if (userToken.getTokenType() != TokenType.PASSWORD_RESET) {
            throw new RuntimeException("Token is not password reset token");
        }
        userToken.setConfirmedAt(LocalDateTime.now());
        userToken.getUser().setPassword(userToken.getEncodedPassword());
        userTokenService.save(userToken);
        userService.save(userToken.getUser());
        log.info("Password changed for user " + userToken.getUser().getEmail());
    }

    /**
     * This method resets password for user with given email
     * @param email
     */

    @Override
    @Transactional
    public void resetPassword(String email) {
        AppUser appUser = (AppUser) userService.loadUserByUsername(email);
        String token = UUID.randomUUID().toString();
        String password = PasswordUtil.generateRandomPassword(PASSWORD_LENGTH);
        UserToken passwordToken = new UserToken(token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                TokenType.PASSWORD_RESET,
                bCryptPasswordEncoder.encode(password),
                appUser);
        userTokenService.save(passwordToken);
        notificationService.sendResetPasswordEmail(appUser, token, password);
    }
}
