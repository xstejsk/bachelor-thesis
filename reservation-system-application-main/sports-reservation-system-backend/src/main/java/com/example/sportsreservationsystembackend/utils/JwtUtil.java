package com.example.sportsreservationsystembackend.utils;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * This class represents jwt utility used for generating and validating jwt tokens
 *
 * @author Radim Stejskal
 */
@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.accessExpirationDateInMs}")
    private long jwtAccessExpirationInMs;

    @Value("${jwt.refreshExpirationDateInMs}")
    private long jwtRefreshExpirationInMs;

    private final UserDetailsService userDetailsService;

    /**
     * This method returns claims from token
     * @param token to be parsed
     * @param claimsResolver to be applied
     * @param <T> type of claims
     * @return claims
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * This method generates a new token
     * @param userDetails to be used for generating token
     * @param isRefresh if token is refresh, otherwise access token is generated
     * @return generated token
     */
    public String generateToken(UserDetails userDetails, boolean isRefresh) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("requestId", UUID.randomUUID());
        return doGenerateToken(claims, userDetails.getUsername(), isRefresh);
    }

    /**
     * This method returns username from token
     * @param token
     * @return
     */

    public String getUserNameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * This method returns user from token
     * @param token to be parsed
     * @return user from token
     */

    public AppUser getUserFromToken(String token) {
        String userName = getUserNameFromToken(token);
        return ((AppUser) userDetailsService.loadUserByUsername(userName));
    }

    /**
     * This method returns issued date from token
     * @param token to be parsed
     * @return issued date from token
     */
    public LocalDateTime getIssuedDateFromToken(String token) {
        return Instant.ofEpochMilli(getClaimFromToken(token, Claims::getIssuedAt).getTime())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * This method returns expiration date from token
     * @param token to be parsed
     * @return expiration date from token
     */
    public LocalDateTime getExpirationDateFromToken(String token) {
        return Instant.ofEpochMilli(getClaimFromToken(token, Claims::getExpiration).getTime())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * This method checks if token is expired
     * @param token to be parsed
     * @return true if token is expired, otherwise false
     */
    public Boolean isTokenExpired(String token) {
        try {
            LocalDateTime expiration = getExpirationDateFromToken(token);
            return expiration.isBefore(LocalDateTime.now());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * This method checks if token is valid
     * @param token to be parsed
     * @throws ExpiredJwtException
     * @throws MalformedJwtException
     */
    public void isTokenValid(String token) throws ExpiredJwtException, MalformedJwtException {
        Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
    }

    private synchronized String doGenerateToken(Map<String, Object> claims, String subject, boolean isRefresh) {
        long ttl = isRefresh ? jwtRefreshExpirationInMs : jwtAccessExpirationInMs;
        log.info("Generating token for subject {} with ttl {}", subject, ttl);
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ttl)).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}