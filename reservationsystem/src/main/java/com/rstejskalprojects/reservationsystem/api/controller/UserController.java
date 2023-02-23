package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.model.dto.AppUserDTO;
import com.rstejskalprojects.reservationsystem.service.UserDetailsServiceImpl;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import com.rstejskalprojects.reservationsystem.util.customexception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
    private final JwtUtil jwtUtil;

    @GetMapping("/logged-user")
    public ResponseEntity<AppUserDTO> getLoggedInUser(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        final String refreshToken = header.split(" ")[1].trim();
        // Get user identity and set it on the spring security context
        AppUser user = (AppUser) userDetailsService.loadUserByUsername(jwtUtil.getUserNameFromToken(refreshToken));
        return new ResponseEntity<>(new AppUserDTO(user), HttpStatus.OK);
    }

    @GetMapping // only admin
    public ResponseEntity<List<AppUserDTO>> getAllUsers(HttpServletRequest request, HttpServletResponse response) {
        List<AppUser> users = userDetailsService.findAll();
        return new ResponseEntity<>(users.stream().map(AppUserDTO::new).collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @PutMapping("/ban/{userId}") // only admin
    public ResponseEntity<String> blockUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token = bearerToken.substring(7);
            AppUser user = (AppUser) userDetailsService.loadUserByUsername(jwtUtil.getUserNameFromToken(token));
            if (user.getUserRole().equals(UserRoleEnum.ADMIN)) {
                userDetailsService.blockUser(userId);
                return new ResponseEntity<>("User blocked", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("You are not admin", HttpStatus.FORBIDDEN);
            }
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping("/unban/{userId}") // only admin
    public ResponseEntity<String> unblockUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token = bearerToken.split(" ")[1].trim();
            AppUser user = (AppUser) userDetailsService.loadUserByUsername(jwtUtil.getUserNameFromToken(token));
            if (user.getUserRole().equals(UserRoleEnum.ADMIN)) {
                userDetailsService.unblockUser(userId);
                return new ResponseEntity<>("User unblocked", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("You are not admin", HttpStatus.FORBIDDEN);
            }
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

    }

    // I want to create an endpoint that promotes user to admin
    @PutMapping("/promote/{userId}") // only admin
    public ResponseEntity<String> promoteUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token = bearerToken.split(" ")[1].trim();
            AppUser user = (AppUser) userDetailsService.loadUserByUsername(jwtUtil.getUserNameFromToken(token));
            if (user.getUserRole().equals(UserRoleEnum.ADMIN)) {
                userDetailsService.promoteUser(userId);
                return new ResponseEntity<>("User promoted", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("You are not admin", HttpStatus.FORBIDDEN);
            }
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/delete/{userId}") // only admin
    public ResponseEntity<String> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token = bearerToken.split(" ")[1].trim();
            AppUser user = (AppUser) userDetailsService.loadUserByUsername(jwtUtil.getUserNameFromToken(token));
            if (user.getUserRole().equals(UserRoleEnum.ADMIN)) {
                userDetailsService.deleteUser(userId);
                return new ResponseEntity<>("User deleted", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("You are not admin", HttpStatus.FORBIDDEN);
            }
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }
}
