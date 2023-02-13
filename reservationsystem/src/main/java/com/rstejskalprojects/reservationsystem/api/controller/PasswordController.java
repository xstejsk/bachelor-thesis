package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.model.PasswordToken;
import com.rstejskalprojects.reservationsystem.service.PasswordResetService;
import com.rstejskalprojects.reservationsystem.service.TokenService;
import com.rstejskalprojects.reservationsystem.util.customexception.ExpiredTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UnknownTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UsedTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordResetService passwordResetService;
    private final TokenService<PasswordToken> passwordTokenService;

    @PostMapping("{email}/forgot-password")
    public ResponseEntity<String> forgotPassword(@PathVariable String email) {
        try {
            passwordResetService.sendResetPasswordEmail(email);
            return new ResponseEntity<>("Password reset link sent to email", HttpStatus.OK);
        }  catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with email: " + email + " not found");
        }
    }

    @PutMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPassword(@PathVariable("token") String token) {
        try {
            PasswordToken resetToken = passwordTokenService.getToken(token);
            passwordResetService.confirmToken(resetToken);
        } catch (UnknownTokenException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found");
        } catch (ExpiredTokenException e) {
            return ResponseEntity.status(HttpStatus.GONE).body("Token expired");
        } catch (UsedTokenException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Token already used");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provided token is invalid");
        }
        return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
    }
}

