package com.example.sportsreservationsystembackend.rest.api;

import com.example.sportsreservationsystembackend.service.AuthService;
import com.xstejsk.reservationapp.main.rest.api.AuthApi;
import com.xstejsk.reservationapp.main.rest.model.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class represents auth controller
 * It is used for authentication and authorization
 *
 * @author Radim Stejskal
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class AuthController implements AuthApi {

    private final AuthService authService;

    /**
     * This method is used for authentication, it supports two grant types: password and refresh_token
     *
     * @param grantType  (optional)
     * @param username  (optional)
     * @param password  (optional)
     * @param refreshToken  (optional)
     * @return ResponseEntity<AuthenticationResponse> object
     */
    @Override
    public ResponseEntity<AuthenticationResponse> authenticate(String grantType, String username, String password, String refreshToken) {
            AuthenticationResponse authenticationResponse = authService.authenticate(grantType, username, password, refreshToken);
            return ResponseEntity.ok().body(authenticationResponse);
    }
}
