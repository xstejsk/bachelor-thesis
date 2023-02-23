package com.rstejskalprojects.reservationsystem.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rstejskalprojects.reservationsystem.repository.UserRepository;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
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
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/auth") || path.startsWith("/api/events") || path.startsWith("/api/reservations")
        || path.startsWith("/api/locations") || path.startsWith("/api/events") || path.startsWith("/api/access") || path.startsWith("/api/users")
                || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");

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
            userDetails = userRepository.findUserByUsername(jwtUtil.getUserNameFromToken(token))
                    .orElseThrow(() ->
                            new UsernameNotFoundException(String.format("Username %s not found for token",
                                    jwtUtil.getUserNameFromToken(token))));
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

