package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.api.model.authorization.AuthCrededentialsRequest;
import com.rstejskalprojects.reservationsystem.api.model.authorization.RegistrationRequest;
import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.dto.AppUserDTO;
import com.rstejskalprojects.reservationsystem.service.RegistrationService;
import com.rstejskalprojects.reservationsystem.service.UserDetailsServiceImpl;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import com.rstejskalprojects.reservationsystem.util.customexception.ExpiredTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UnknownTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UsedTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final RegistrationService registrationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated AuthCrededentialsRequest request, HttpServletResponse response) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(), request.getPassword()
                            )
                    );
            AppUser user = (AppUser) authenticate.getPrincipal();
            String accessToken = jwtUtil.generateToken(user, false);
            String refreshToken = jwtUtil.generateToken(user, true);
            Cookie cookie = new Cookie("refresh_token", refreshToken);
            cookie.setMaxAge((int) (jwtUtil.getJwtRefreshExpirationInMs() / 1000));
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            return ResponseEntity.ok()
                    .body(new HashMap<String, Object>(){{
                        put("access_token", accessToken);
                        put("user", new AppUserDTO(user));
                    }});

        } catch (BadCredentialsException ex) {
            log.warn("Bad credentials for user: " + request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (DisabledException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request) {
        if (userDetailsService.existsByUsername(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        String token = registrationService.register(request);
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    @PostMapping("{emailAddress}/resend-confirmation-email")
    public ResponseEntity<String> resendRegistrationToken(@PathVariable String emailAddress) {
        try {
            String token = registrationService.resendRegistrationEmail(emailAddress);
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (UsernameNotFoundException ex) {
            log.warn("User not found: " + emailAddress);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/confirm-registration/{token}")
        public ResponseEntity<String> confirmRegistration(@PathVariable String token) {
        try {
            registrationService.confirmToken(token);
            return ResponseEntity.ok().build();
        } catch (UsedTokenException e) {
            log.warn("Token already used: " + token);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (ExpiredTokenException e) {
            log.warn("Token expired: " + token);
            return ResponseEntity.status(HttpStatus.GONE).build();
        } catch (UnknownTokenException e) {
            log.warn("Token unknown: " + token);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.warn("Unknown error: " + token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        // Clear the server-side session data
        request.getSession().invalidate();

        // Clear the "refresh_token" cookie
        Cookie refreshToken = new Cookie("refresh_token", "");
        refreshToken.setMaxAge(0);
        refreshToken.setPath("/");
        refreshToken.setHttpOnly(true);
        Cookie jsessionid = new Cookie("JSESSIONID", "");
        jsessionid.setMaxAge(0);
        jsessionid.setPath("/");
        jsessionid.setHttpOnly(true);
        response.addCookie(refreshToken);
        response.addCookie(jsessionid);
        return ResponseEntity.ok().build();
    }

}
