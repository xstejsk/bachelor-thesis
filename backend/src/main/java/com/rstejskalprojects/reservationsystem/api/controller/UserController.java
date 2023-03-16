package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.dto.AppUserDTO;
import com.rstejskalprojects.reservationsystem.service.UserDetailsServiceImpl;
import com.rstejskalprojects.reservationsystem.util.customexception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping
    public ResponseEntity<List<AppUserDTO>> getAllUsers(HttpServletRequest request, HttpServletResponse response) {
        List<AppUser> users = userDetailsService.findAll();
        return new ResponseEntity<>(users.stream().map(AppUserDTO::new).collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @PutMapping("/ban/{userId}")
    public ResponseEntity<String> blockUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            userDetailsService.blockUser(userId);
            return new ResponseEntity<>("User blocked", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/unban/{userId}") // only admin
    public ResponseEntity<String> unblockUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            userDetailsService.unblockUser(userId);
            return new ResponseEntity<>("User unblocked", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/promote/{userId}") // only superadmin
    public ResponseEntity<String> promoteUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            userDetailsService.promoteUser(userId);
            return new ResponseEntity<>("User promoted", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{userId}") // only admin
    public ResponseEntity<String> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            userDetailsService.deleteUser(userId);
            return new ResponseEntity<>("User deleted", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }
}
