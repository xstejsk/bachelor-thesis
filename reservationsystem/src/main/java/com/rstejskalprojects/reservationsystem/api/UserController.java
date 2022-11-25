package com.rstejskalprojects.reservationsystem.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public void getLoggedInUser(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        final String refreshToken = header.split(" ")[1].trim();
        // Get user identity and set it on the spring security context
        AppUser user = (AppUser) userDetailsService.loadUserByUsername(jwtUtil.getUserNameFromToken(refreshToken));
        Map<String, String> tokens = new HashMap<>();
        tokens.put("name", user.getFirstName());
        tokens.put("username", user.getUsername());
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            response.setHeader("Error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.warn("refresh token could not be sent", e);
        }
    }

}
