package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.dto.AppUserDTO;
import com.rstejskalprojects.reservationsystem.service.UserDetailsServiceImpl;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
@Api(value = "User Management System", description = "Operations pertaining to user in User Management System")
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
    @ApiOperation(value = "View a list of all users", response = List.class)
    public ResponseEntity<List<AppUserDTO>> getAllUsers(HttpServletRequest request, HttpServletResponse response) {
        List<AppUser> users = userDetailsService.findAll();
        return new ResponseEntity<>(users.stream().map(AppUserDTO::new).collect(Collectors.toList()),
                HttpStatus.OK);
    }
}
