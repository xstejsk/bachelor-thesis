package com.rstejskalprojects.reservationsystem.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rstejskalprojects.reservationsystem.api.model.authorization.AuthCredentialsRequest;
import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.dto.AppUserDTO;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
@Slf4j
public class TokensController {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String refreshToken = header.split(" ")[1].trim();
            UserDetails userDetails = userDetailsService.loadUserByUsername(jwtUtil.getUserNameFromToken(refreshToken));
            String accessToken = jwtUtil.generateToken(userDetails, false);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);
            return new ResponseEntity<>(tokens, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<?> login(@Valid @RequestBody AuthCredentialsRequest request) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(), request.getPassword()
                            )
                    );
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            AppUser user = (AppUser) authenticate.getPrincipal();
            String accessToken = jwtUtil.generateToken(user, false);
            String refreshToken = jwtUtil.generateToken(user, true);
            return ResponseEntity.ok()
                    .body(new HashMap<String, Object>(){{
                        put("access_token", accessToken);
                        put("refresh_token", refreshToken);
                        put("user", new AppUserDTO(user));
                    }});

        } catch (BadCredentialsException ex) {
            log.warn("Bad credentials for user: " + request.getUsername());
            return new ResponseEntity<>("Bad credentials", HttpStatus.UNAUTHORIZED);
        } catch (DisabledException e) {
            return new ResponseEntity<>("Account not verified", HttpStatus.FORBIDDEN);
        } catch (LockedException e) {
            return new ResponseEntity<>("Account locked", HttpStatus.FORBIDDEN);
        }
    }
}
