package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.util.customexception.ExpiredTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UnknownTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UsedTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ConfirmationTokeController {

    private final TokenService token;

    @PutMapping("/confirmation/{token}")
    public ResponseEntity<String> confirmRegistration(@PathVariable String token) {
        try {
            registrationService.confirmToken(token);
            return new ResponseEntity<>("Registration confirmed", HttpStatus.OK);
        } catch (UsedTokenException e) {
            log.warn("Token already used: " + token);
            return new ResponseEntity<>("Token already used", HttpStatus.CONFLICT);
        } catch (ExpiredTokenException e) {
            log.warn("Token expired: " + token);
            return new ResponseEntity<>("Token expired", HttpStatus.BAD_REQUEST);
        } catch (UnknownTokenException e) {
            log.warn("Token unknown: " + token);
            return new ResponseEntity<>("Token unknown", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.warn("Unknown error: " + token);
            return new ResponseEntity<>("Unknown error", HttpStatus.BAD_REQUEST);
        }
    }
}
