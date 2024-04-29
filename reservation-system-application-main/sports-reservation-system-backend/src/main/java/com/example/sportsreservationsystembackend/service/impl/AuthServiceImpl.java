package com.example.sportsreservationsystembackend.service.impl;

import com.example.sportsreservationsystembackend.exceptions.UnsupportedGrantTypeException;
import com.example.sportsreservationsystembackend.exceptions.InvalidRefreshTokenException;
import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.RefreshToken;
import com.example.sportsreservationsystembackend.rest.mapper.AppUserMapper;
import com.example.sportsreservationsystembackend.service.AuthService;
import com.example.sportsreservationsystembackend.service.RefreshTokenService;
import com.example.sportsreservationsystembackend.service.UserService;
import com.example.sportsreservationsystembackend.utils.JwtUtil;
import com.xstejsk.reservationapp.main.rest.model.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This class represents auth service implementation
 *
 * @author Radim Stejskal
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AppUserMapper appUserMapper;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;


    /**
     * This method is used for building authentication response
     * @param grantType
     * @param username
     * @param password
     * @param refreshToken
     * @return authentication response
     */
    @Override
    public AuthenticationResponse authenticate(String grantType, String username, String password, String refreshToken) {
        if (Objects.equals(grantType, OAuth2Constants.PASSWORD)) {
            return authenticateWithPasswordGrant(username, password);
        } else if (Objects.equals(grantType, OAuth2Constants.REFRESH_TOKEN)) {
            return refreshAccessToken(refreshToken);
        } else {
            throw new UnsupportedGrantTypeException("GrantType " + grantType + " is not supported");
        }
    }

    private AuthenticationResponse authenticateWithPasswordGrant(String username, String password) {
        log.info("Authenticating user with username {}", username);
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            AppUser user = (AppUser) authenticate.getPrincipal();
            String accessToken = jwtUtil.generateToken(user, false);
            String newRefreshToken = jwtUtil.generateToken(user, true);
            refreshTokenService.save(new RefreshToken().setToken(newRefreshToken).setUser(user).setExpiresAt(
                    jwtUtil.getExpirationDateFromToken(newRefreshToken)
            ));
            return buildAuthenticationResponse(user, accessToken, newRefreshToken);
        } catch (DisabledException e) {
            AppUser user = (AppUser) userService.loadUserByUsername(username);
            userService.sendRegistrationToken(user);
            throw new DisabledException("User is disabled", e);
        }
    }

    private AuthenticationResponse refreshAccessToken(String refreshTokenValue) {
        log.info("Refreshing token");
        RefreshToken oldRefreshToken = refreshTokenService.findByToken(refreshTokenValue);
        if (oldRefreshToken.getUsedAt() != null) {
            throw new InvalidRefreshTokenException("Refresh token has already been used");
        }
        if (oldRefreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("Refresh token has expired with expiration date {}", oldRefreshToken.getExpiresAt());
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }
        oldRefreshToken.setUsedAt(LocalDateTime.now());
        refreshTokenService.save(oldRefreshToken);

        AppUser user = oldRefreshToken.getUser();
        String accessToken = jwtUtil.generateToken(user, false);
        String newRefreshTokenValue = jwtUtil.generateToken(user, true);
        refreshTokenService.save(new RefreshToken().setUser(user).setToken(newRefreshTokenValue).setExpiresAt(
                jwtUtil.getExpirationDateFromToken(newRefreshTokenValue)
        ));
        return buildAuthenticationResponse(user, accessToken, newRefreshTokenValue);
    }

    private AuthenticationResponse buildAuthenticationResponse(AppUser user, String accessToken, String refreshToken) {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccessToken(accessToken);
        authenticationResponse.setRefreshToken(refreshToken);
        authenticationResponse.setUser(appUserMapper.appUserToAppUserDTO(user));
        return authenticationResponse;
    }
}