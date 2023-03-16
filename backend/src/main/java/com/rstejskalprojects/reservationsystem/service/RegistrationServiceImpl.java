package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.api.model.authorization.RegistrationRequest;
import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.model.RegistrationToken;
import com.rstejskalprojects.reservationsystem.util.customexception.ExpiredTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UnknownTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UsedTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    private final UserDetailsServiceImpl userDetailsService;
    private final ConfirmationTokenServiceImpl registrationTokenService;
    private final EmailSender emailSender;
    private final EmailFormatterService emailFormatterService;
    @Value("${host.domain}")
    private String host;

    public String register(RegistrationRequest request){
        String token = userDetailsService.saveUser(
                new AppUser(request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        UserRoleEnum.USER));

        String link = host + "/registration/confirm/?token=" + token;
        new Thread(() -> emailSender.sendEmail(request.getEmail(), buildEmail(request.getFirstName(), link), "Potvrzení registrace")).start();
        return token;
    }

    @Override
    public String resendRegistrationEmail(String email) {
        try {
            AppUser appUser = (AppUser) userDetailsService.loadUserByUsername(email);
            String token = UUID.randomUUID().toString();
            RegistrationToken confirmationToken = new RegistrationToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(30),
                    appUser);

            registrationTokenService.saveToken(confirmationToken);
            String link = host + "/registration/confirm?token=" + token;
            new Thread(() -> emailSender.sendEmail(email, buildEmail(appUser.getFirstName(), link), "Potvrzení registrace")).start();
            log.info("Registration token for user {} was resent", email);
            return token;
        } catch (UsernameNotFoundException e) {
            log.warn("User {} not found", email);
            throw new UsernameNotFoundException("user with given email does not exist");
        }
    }

    @Transactional
    public void confirmToken(String token) throws UnknownTokenException {
        RegistrationToken confirmationToken = registrationTokenService
                .getToken(token);

        if (confirmationToken.getConfirmedAt() != null) {
            throw new UsedTokenException("Email is already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException("Token has expired");
        }

        registrationTokenService.setConfirmedAt(confirmationToken);
        userDetailsService.enableUser(confirmationToken.getUser().getEmail());
        log.info("User {} was enabled", confirmationToken.getUser().getEmail());
    }

    private String buildEmail(String name, String link) {
        return emailFormatterService.formatRegistrationConfirmationEmail(name, link, 30);
    }
}