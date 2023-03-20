package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.api.controller.model.ConfirmationTokenRequest;
import com.rstejskalprojects.reservationsystem.api.controller.model.EmailRequest;
import com.rstejskalprojects.reservationsystem.model.UserToken;
import com.rstejskalprojects.reservationsystem.service.RegistrationService;
import com.rstejskalprojects.reservationsystem.service.UserTokenService;
import com.rstejskalprojects.reservationsystem.util.customexception.ExpiredTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UnknownTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UsedTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/confirmations")
@RequiredArgsConstructor
@Slf4j
public class ConfirmationTokenController {

    private final UserTokenService userTokenService;
    private final RegistrationService registrationService;

    @PutMapping("/submit-token")
    public ResponseEntity<String> submitToken(@RequestBody @Valid ConfirmationTokenRequest confirmationTokenRequest) {
        try {
            String token = confirmationTokenRequest.getToken();
            UserToken userToken = userTokenService.getToken(token);
            userTokenService.confirmToken(userToken);
            return new ResponseEntity<>(userToken.getTokenType().getName() + " token submitted successfully", HttpStatus.OK);
        } catch (UnknownTokenException e) {
            log.warn("Unknown token: {}", confirmationTokenRequest.getToken());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found");
        } catch (ExpiredTokenException e) {
            log.warn("Expired token: {}", confirmationTokenRequest.getToken());
            return ResponseEntity.status(HttpStatus.GONE).body("Token expired");
        } catch (UsedTokenException e) {
            log.warn("Used token: {}", confirmationTokenRequest.getToken());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Token already used");
        } catch (Exception e) {
            log.warn("Unknown error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provided token is invalid");
        }
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<String> resendRegistrationToken(@RequestBody @Valid EmailRequest emailAddress) {
        try {
            registrationService.resendRegistrationEmail(emailAddress.getEmail());
            return new ResponseEntity<>("Registration email has been resent to the provided email address", HttpStatus.OK);
        } catch (UsernameNotFoundException ex) {
            log.warn("User not found: " + emailAddress);
            return new ResponseEntity<>("User with given email not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error while sending registration email to: " + emailAddress, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
