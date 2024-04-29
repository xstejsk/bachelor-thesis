package com.example.sportsreservationsystembackend.rest.api;

import com.example.sportsreservationsystembackend.service.PasswordResetService;
import com.example.sportsreservationsystembackend.service.UserService;
import com.xstejsk.reservationapp.main.rest.api.UsersApi;
import com.xstejsk.reservationapp.main.rest.model.AppUserDTO;
import com.xstejsk.reservationapp.main.rest.model.ChangePasswordRequest;
import com.xstejsk.reservationapp.main.rest.model.CreateUserRequest;
import com.xstejsk.reservationapp.main.rest.model.ResetPasswordRequest;
import com.xstejsk.reservationapp.main.rest.model.UpdateDiscountRequest;
import com.xstejsk.reservationapp.main.rest.model.UpdateUserBalanceRequest;
import com.xstejsk.reservationapp.main.rest.model.UpdateUserRoleRequest;
import com.xstejsk.reservationapp.main.rest.model.UsersPage;
import com.xstejsk.reservationapp.main.rest.model.VerificationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class represents user controller
 *
 * @author Radim Stejskal
 */

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    /**
     * This method is used for registration of new user
     * @param createUserRequest  (required)
     * @return new user
     */
    @Override
    public ResponseEntity<AppUserDTO> createUser(CreateUserRequest createUserRequest) {
        AppUserDTO appUserDTO = userService.createUser(createUserRequest);
        return ResponseEntity.created(null).body(appUserDTO);
    }

    /**
     * This method is used for resetting user password, the new password is sent to user email and must be confirmed
     * @param resetPasswordRequest  (required)
     * @return
     */
    @Override
    public ResponseEntity<Void> resetForgottenPassword(ResetPasswordRequest resetPasswordRequest) {
        passwordResetService.resetPassword(resetPasswordRequest.getEmail());
        return ResponseEntity.accepted().build();
    }

    /**
     * This method is used for getting all users
     * @param page  (optional)
     * @param size page size, default is 50 (optional)
     * @param fulltext  (optional)
     * @param role  (optional)
     * @return page of users
     */

    @Override
    public ResponseEntity<UsersPage> getUsers(Integer page, Integer size, String fulltext, String role) {
        log.info("getUsers: page={}, size={}, fulltext={}, role={}", page, size, fulltext, role);
        UsersPage usersPage = userService.getAll(fulltext, role, page, size);
        return ResponseEntity.ok().body(usersPage);
    }

    /**
     * This method is used for updating user's balance
     * @param userId  (required)
     * @param updateUserBalanceRequest  (required)
     * @return updated user
     */
    @Override
    public ResponseEntity<AppUserDTO> updateUserBalance(String userId, UpdateUserBalanceRequest updateUserBalanceRequest) {
        AppUserDTO appUserDTO = userService.updateUserBalance(userId, updateUserBalanceRequest.getBalance());
        return ResponseEntity.ok().body(appUserDTO);
    }

    /**
     * This method is used for updating user's role
     * @param userId  (required)
     * @param updateUserRoleRequest  (required)
     * @return updated user
     */
    @Override
    public ResponseEntity<AppUserDTO> updateUserRole(String userId, UpdateUserRoleRequest updateUserRoleRequest) {
        AppUserDTO appUserDTO = userService.updateUserRole(userId, updateUserRoleRequest.getRole());
        return ResponseEntity.ok().body(appUserDTO);
    }

    /**
     * This method is used for verifying user and activating his account
     * @param verificationToken  (required)
     * @return verified user
     */
    @Override
    public ResponseEntity<AppUserDTO> verifyUser(VerificationToken verificationToken) {
        log.info("Verifying user with token {}", verificationToken.getToken());
        AppUserDTO appUserDTO = userService.verifyUser(verificationToken.getToken());
        return ResponseEntity.ok().body(appUserDTO);
    }

    /**
     * This method is used for changing user's password
     * @param userId  (required)
     * @param changePasswordRequest  (required)
     * @return updated user
     */
    @Override
    public ResponseEntity<AppUserDTO> changeUsersPassword(String userId, ChangePasswordRequest changePasswordRequest) {
        log.info("Changing password for user with id {}", userId);
        AppUserDTO appUserWithReservationsDTO = userService.changePassword(userId, changePasswordRequest);
        return ResponseEntity.ok().body(appUserWithReservationsDTO);
    }

    /**
     * This method is used for confirming password reset and setting new password
     * @param verificationToken  (required)
     * @return
     */
    @Override
    public ResponseEntity<Void> confirmPasswordReset(VerificationToken verificationToken) {
        passwordResetService.submitPasswordChange(verificationToken.getToken());
        return ResponseEntity.ok().build();
    }

    /**
     * This method is used for updating user's discount entitlement
     * @param userId  (required)
     * @param updateDiscountRequest  (required)
     * @return updated user
     */
    @Override
    public ResponseEntity<AppUserDTO> updateUsersDiscountEntitlement(String userId, UpdateDiscountRequest updateDiscountRequest) {
        AppUserDTO appUserDTO = userService.updateDiscountEntitlement(userId, updateDiscountRequest.getDiscountActive());
        return ResponseEntity.ok().body(appUserDTO);
    }
}
