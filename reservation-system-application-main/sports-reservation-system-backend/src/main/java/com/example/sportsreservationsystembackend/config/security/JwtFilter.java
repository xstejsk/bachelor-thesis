package com.example.sportsreservationsystembackend.config.security;

import com.example.sportsreservationsystembackend.service.UserService;
import com.example.sportsreservationsystembackend.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to filter requests and check if the JWT token is valid
 * @author Radim Stejskal
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        log.info("Path in JWT filter: " + path);
        return path.startsWith("/api/v1/auth") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") ||
                (path.startsWith("/api/v1/users") && request.getMethod().equals("POST")) ||
                (path.startsWith("/api/v1/calendars") && request.getMethod().equals("GET")) ||
                (path.startsWith("/api/v1/locations") && request.getMethod().equals("GET")) ||
                path.startsWith("/api/v1/users/verification") || path.startsWith("/api/v1/users/password");

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ") || header.length() < 8) {
            response.setHeader("Error", "Missing bearer token.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.warn("request does not contain header with bearer, path: {}", request.getServletPath());
            return;
        }
        final String token = header.split(" ")[1].trim();

        UserDetails userDetails;
        try {
            jwtUtil.isTokenValid(token);
            userDetails = userService.loadUserByUsername(jwtUtil.getUserNameFromToken(token));
        } catch (ExpiredJwtException | MalformedJwtException e) {
            log.info("Invalid token filtered {} ", token, e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            new ObjectMapper().writeValue(response.getOutputStream(), body);
            return;
        } catch (UsernameNotFoundException e) {
            log.info("User with the username from the token {} not found ", token, e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            new ObjectMapper().writeValue(response.getOutputStream(), body);
            return;
        }
        // Get user identity and set it on the spring security context
        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails.getAuthorities()
        );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}


