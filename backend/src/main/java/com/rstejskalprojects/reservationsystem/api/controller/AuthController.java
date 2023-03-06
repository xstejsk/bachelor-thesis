package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.api.model.authorization.AuthCredentialsRequest;
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
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
    public ResponseEntity<?> login(@Valid @RequestBody AuthCredentialsRequest request, HttpServletResponse response) {
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (DisabledException e) {
            return new ResponseEntity<>("Account not verified", HttpStatus.FORBIDDEN);
        } catch (LockedException e) {
            return new ResponseEntity<>("Account locked", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest request) {
        if (userDetailsService.existsByUsername(request.getEmail())) {
            return new ResponseEntity<>("This email is already registered", HttpStatus.CONFLICT);
        }
        registrationService.register(request);
        return new ResponseEntity<>("Confirmation email has been sent to the provided email address", HttpStatus.CREATED);
    }

    @PostMapping("{emailAddress}/resend-confirmation-email")
    public ResponseEntity<String> resendRegistrationToken(@PathVariable String emailAddress) {
        try {
            registrationService.resendRegistrationEmail(emailAddress);
            return new ResponseEntity<>("Registration email has been resent to the provided email address", HttpStatus.OK);
        } catch (UsernameNotFoundException ex) {
            log.warn("User not found: " + emailAddress);
            return new ResponseEntity<>("User with given email not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/confirm-registration/{token}")
        public ResponseEntity<String> confirmRegistration(@PathVariable String token) {
        try {
            registrationService.confirmToken(token);
            return new ResponseEntity<>("Registration confirmed", HttpStatus.OK);
        } catch (UsedTokenException e) {
            log.warn("Token already used: " + token);
            return new ResponseEntity<>("Token already used", HttpStatus.CONFLICT);
        } catch (ExpiredTokenException e) {
            log.warn("Token expired: " + token);
            return new ResponseEntity<>("Token expired", HttpStatus.BAD_REQUEST);
        } catch (UnknownTokenException e) {
            log.warn("Token unknown: " + token);
            return new ResponseEntity<>("Token unknown", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.warn("Unknown error: " + token);
            return new ResponseEntity<>("Unknown error", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // Clear the server-side session data
        request.getSession().invalidate();

        // Clear the "refresh_token" cookie
        Cookie jsessionid = new Cookie("JSESSIONID", "");
        jsessionid.setMaxAge(0);
        jsessionid.setPath("/");
        jsessionid.setHttpOnly(true);
        response.addCookie(jsessionid);
        return ResponseEntity.ok().build();
    }

}
