package com.rstejskalprojects.reservationsystem.util;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.service.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Getter
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.accessExpirationDateInMs}")
    private long jwtAccessExpirationInMs;

    @Value("${jwt.refreshExpirationDateInMs}")
    private long jwtRefreshExpirationInMs;

    private final UserDetailsServiceImpl userDetailsService;

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails, boolean isRefresh) {
        Map<String, Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();
        if (roles.contains(new SimpleGrantedAuthority(UserRoleEnum.ADMIN.getName()))) {
            claims.put("isAdmin", true);
        }
        if (roles.contains(new SimpleGrantedAuthority(UserRoleEnum.SUPER_ADMIN.getName()))) {
            claims.put("isSuperAdmin", true);
        }
        if (roles.contains(new SimpleGrantedAuthority(UserRoleEnum.USER.getName()))) {
            claims.put("isUser", true);
        }
        return doGenerateToken(claims, userDetails.getUsername(), isRefresh);
    }

    public String getUserNameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public AppUser getUserFromToken(String token) {
        String userName = getUserNameFromToken(token);
        return ((AppUser) userDetailsService.loadUserByUsername(userName));
    }

    public LocalDateTime getIssuedDateFromToken(String token) {
        return Instant.ofEpochMilli(getClaimFromToken(token, Claims::getIssuedAt).getTime())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public LocalDateTime getExpirationDateFromToken(String token) {
        return Instant.ofEpochMilli(getClaimFromToken(token, Claims::getExpiration).getTime())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public Boolean isTokenExpired(String token) {
        try {
            LocalDateTime expiration = getExpirationDateFromToken(token);
            return expiration.isBefore(LocalDateTime.now());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public void isTokenValid(String token) throws ExpiredJwtException, MalformedJwtException{
        Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, boolean isRefresh) {
        long ttl = isRefresh ? jwtRefreshExpirationInMs : jwtAccessExpirationInMs;
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ttl)).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}