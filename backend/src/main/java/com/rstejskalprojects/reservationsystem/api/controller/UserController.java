package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.api.controller.model.BanstatusRequest;
import com.rstejskalprojects.reservationsystem.api.controller.model.ChangeRoleRequest;
import com.rstejskalprojects.reservationsystem.api.controller.model.EmailRequest;
import com.rstejskalprojects.reservationsystem.api.model.authorization.RegistrationRequest;
import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.model.dto.AppUserDTO;
import com.rstejskalprojects.reservationsystem.service.PasswordResetService;
import com.rstejskalprojects.reservationsystem.service.RegistrationService;
import com.rstejskalprojects.reservationsystem.service.UserDetailsServiceImpl;
import com.rstejskalprojects.reservationsystem.util.customexception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserDetailsServiceImpl userDetailsService;
    private final RegistrationService registrationService;
    private final PasswordResetService passwordResetService;

    @GetMapping
    public ResponseEntity<List<AppUserDTO>> getAllUsers(HttpServletRequest request, HttpServletResponse response) {
        List<AppUser> users = userDetailsService.findAll();
        return new ResponseEntity<>(users.stream().map(AppUserDTO::new).collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @PutMapping("/{userId}/ban-status") // only admin
    public ResponseEntity<String> unblockUser(@RequestBody @Valid BanstatusRequest banstatusRequest, @PathVariable Long userId, HttpServletRequest request) {
        try {
            String status;
            if (banstatusRequest.getBanned()) {
                userDetailsService.blockUser(userId);
                status = "User with id " + userId + " was blocked";
            } else {
                userDetailsService.unblockUser(userId);
                status = "User with id " + userId + " was unblocked";
            }
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{userId}/role") // only superadmin
    public ResponseEntity<String> changeUserRole(@RequestBody @Valid ChangeRoleRequest changeRoleRequest, @PathVariable Long userId, HttpServletRequest request) {
        try {
            if (changeRoleRequest.getRole().equals(UserRoleEnum.ADMIN.getName())) {
                userDetailsService.promoteUser(userId);
                return new ResponseEntity<>("User promoted", HttpStatus.OK);
            } else if (changeRoleRequest.getRole().equals(UserRoleEnum.USER.getName())) {
                userDetailsService.demoteUser(userId);
                return new ResponseEntity<>("User demoted", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid role", HttpStatus.BAD_REQUEST);
            }
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{userId}") // only admin
    public ResponseEntity<String> deleteUser(@PathVariable Long userId,  HttpServletRequest request) {
        try {
            userDetailsService.deleteUser(userId);
            return new ResponseEntity<>("User deleted", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid EmailRequest forgotPasswordRequest, @PathVariable Long userId, HttpServletRequest request) {
        try {
            passwordResetService.sendResetPasswordEmail(forgotPasswordRequest.getEmail());
            return new ResponseEntity<>("Password reset link sent to email", HttpStatus.OK);
        }  catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with email: " + forgotPasswordRequest.getEmail() + " not found");
        }
    }

    @PostMapping
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest request) {
        if (userDetailsService.existsByUsername(request.getEmail())) {
            return new ResponseEntity<>("This email is already registered", HttpStatus.CONFLICT);
        }
        registrationService.register(request);
        return new ResponseEntity<>("Confirmation email has been sent to the provided email address", HttpStatus.CREATED);
    }
}
