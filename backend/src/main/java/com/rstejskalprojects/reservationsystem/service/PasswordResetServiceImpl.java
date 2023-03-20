package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.NonPersistentPasswordToken;
import com.rstejskalprojects.reservationsystem.model.TokenTypeEnum;
import com.rstejskalprojects.reservationsystem.model.UserToken;
import com.rstejskalprojects.reservationsystem.repository.UserTokenRepository;
import com.rstejskalprojects.reservationsystem.util.customexception.ExpiredTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UsedTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailSender emailSender;
    private final EmailFormatterService emailFormatterService;
    private final UserTokenRepository userTokenRepository;
    @Value("${host.domain}")
    private String host;
    private final static int PASSWORD_LENGTH = 8;

    @Override
    public void sendResetPasswordEmail(String email) {
        try {
            AppUser appUser = (AppUser) userDetailsService.loadUserByUsername(email);
            NonPersistentPasswordToken token = createPasswordResetTokenForUser(appUser);
            String link = host + "/password-reset/confirm?token=" + token.getToken();
            new Thread(() -> emailSender.sendEmail(email, buildEmail(appUser.getFirstName(), link, token.getPassword()), "Reset hesla")).start();
            log.info("Email with password reset link was sent to " + email);
        } catch (UsernameNotFoundException e) {
            log.warn("User {} not found", email);
            throw new UsernameNotFoundException("user with given email does not exist");
        }
    }

    private NonPersistentPasswordToken createPasswordResetTokenForUser(AppUser appUser) {
        String token = UUID.randomUUID().toString();
        String password = generateRandomPassword(PASSWORD_LENGTH);
        UserToken passwordToken = new UserToken(token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                TokenTypeEnum.PASSWORD_RESET,
                bCryptPasswordEncoder.encode(password),
                appUser);
        userTokenRepository.save(passwordToken);
        return new NonPersistentPasswordToken(passwordToken.getToken(), passwordToken.getUser(), password);
    }

    @Override
    public void confirmToken(UserToken passwordToken) {
        if (passwordToken.getConfirmedAt() != null) {
            throw new UsedTokenException("Password was already set");
        }
        if (passwordToken.getTokenType() != TokenTypeEnum.PASSWORD_RESET) {
            throw new IllegalArgumentException("Token is not password reset token");
        }
        LocalDateTime expiredAt = passwordToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException("Token has expired");
        }
        AppUser appUser = passwordToken.getUser();
        String encodedPassword = passwordToken.getEncodedPassword();
        userDetailsService.changeUserEncodedPassword(appUser, encodedPassword);
        log.info("Password for user " + appUser.getUsername() + " was changed");
    }

    public static String generateRandomPassword(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi"
                +"jklmnopqrstuvwxyz";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    private String buildEmail(String recipient, String link, String password) {
        return emailFormatterService.formatPasswordResetEmail(recipient, link, 30, password);
    }
}
