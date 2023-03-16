package com.rstejskalprojects.reservationsystem.util;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationUtil {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public boolean userIdMatchesJWT(Long userId, String token) {
        if (token == null) {
            return false;
        }
        if (token.startsWith("Bearer")) {
            token = token.substring(7);
        }
        String username = jwtUtil.getUserNameFromToken(token);
        AppUser appUser = userDetailsService.loadUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return appUser.getUsername().equals(username);
    }

//    public boolean isAdmin(String token) {
//        if (token == null) {
//            return false;
//        }
//        if (token.startsWith("Bearer")) {
//            token = token.substring(7);
//        }
//        return ((AppUser)userDetailsService.loadUserByUsername(jwtUtil.getUserNameFromToken(token))).getUserRole().getName().equals("ROLE_ADMIN");
//    }
}
