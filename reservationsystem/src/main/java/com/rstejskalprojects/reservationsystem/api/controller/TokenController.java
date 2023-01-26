package com.rstejskalprojects.reservationsystem.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @GetMapping("refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        final String refreshToken = header.split(" ")[1].trim();
        // Get user identity and set it on the spring security context
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtUtil.getUserNameFromToken(refreshToken));
        String accessToken = jwtUtil.generateToken(userDetails, false);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        } catch (IOException e) {
            response.setHeader("Error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.warn("refresh token could not be sent", e);
        }
    }
}
