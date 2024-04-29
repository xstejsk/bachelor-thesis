package com.example.sportsreservationsystembackend.service;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.xstejsk.reservationapp.main.rest.model.AppUserDTO;
import com.xstejsk.reservationapp.main.rest.model.ChangePasswordRequest;
import com.xstejsk.reservationapp.main.rest.model.CreateUserRequest;
import com.xstejsk.reservationapp.main.rest.model.UpdateUserRoleRequest;
import com.xstejsk.reservationapp.main.rest.model.UsersPage;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    AppUserDTO createUser(CreateUserRequest createUserRequest);

    AppUserDTO verifyUser(String token);

    void sendRegistrationToken(AppUser appUser);

    AppUser getCurrentUser();

    AppUser save(AppUser appUser);

    UsersPage getAll(String fulltext, String role, Integer page, Integer size);

    AppUserDTO updateUserBalance(String userId, Integer balance);

    AppUserDTO updateUserRole(String userId, UpdateUserRoleRequest.RoleEnum role);

    AppUser getUserById(String userId);

    AppUserDTO changePassword(String userId, ChangePasswordRequest changePasswordRequest);

    AppUserDTO updateDiscountEntitlement(String userId, boolean hasDiscountEntitlement);
}
