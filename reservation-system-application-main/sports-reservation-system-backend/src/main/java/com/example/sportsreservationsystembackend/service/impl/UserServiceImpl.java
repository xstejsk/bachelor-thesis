package com.example.sportsreservationsystembackend.service.impl;

import com.example.sportsreservationsystembackend.service.config.RegistrationTemplateConfig;
import com.example.sportsreservationsystembackend.exceptions.EmailTakenException;
import com.example.sportsreservationsystembackend.exceptions.LastAdminException;
import com.example.sportsreservationsystembackend.exceptions.PasswordMissmatchException;
import com.example.sportsreservationsystembackend.exceptions.ResourceNotOwnedException;
import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.TokenType;
import com.example.sportsreservationsystembackend.model.UserRole;
import com.example.sportsreservationsystembackend.model.UserToken;
import com.example.sportsreservationsystembackend.repository.UserRepository;
import com.example.sportsreservationsystembackend.rest.mapper.AppUserMapper;
import com.example.sportsreservationsystembackend.rest.mapper.PageMapper;
import com.example.sportsreservationsystembackend.service.NotificationService;
import com.example.sportsreservationsystembackend.service.UserService;
import com.example.sportsreservationsystembackend.service.UserTokenService;
import com.xstejsk.reservationapp.main.rest.model.AppUserDTO;
import com.xstejsk.reservationapp.main.rest.model.ChangePasswordRequest;
import com.xstejsk.reservationapp.main.rest.model.CreateUserRequest;
import com.xstejsk.reservationapp.main.rest.model.UpdateUserRoleRequest;
import com.xstejsk.reservationapp.main.rest.model.UsersPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents user service implementation
 *
 * @author Radim Stejskal
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final AppUserMapper appUserMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserTokenService userTokenService;
    private final NotificationService emailService;
    private final RegistrationTemplateConfig registrationTemplateConfig;
    private final UserTokenService tokenService;
    private final PageMapper pageMapper;

    /**
     * This method returns a user by id
     * @param userId id of user
     * @return found user
     */
    @Override
    public AppUser getUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * This method creates a new user
     * @param createUserRequest request for creating a new user
     * @return created user
     */
    @Override
    public AppUserDTO createUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new EmailTakenException("User with username " + createUserRequest.getEmail() + " already exists");
        }
        validateCreateUserRequest(createUserRequest);
        AppUser appUser = appUserMapper.createRequestToAppUser(createUserRequest);
        appUser.setRole(UserRole.USER);
        if (createUserRequest.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        appUser.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        AppUser savedAppUser = userRepository.save(appUser);
        sendRegistrationToken(savedAppUser);
        return appUserMapper.appUserToAppUserDTO(savedAppUser);
    }

    /**
     * This method creates a new registration token and sends it to user's email
     * @param appUser user to send token to
     */

    @Override
    public void sendRegistrationToken(AppUser appUser) {
        String token = UUID.randomUUID().toString();
        UserToken confirmationToken = new UserToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(Integer.parseInt(registrationTemplateConfig.getExpirationMinutes())),
                TokenType.REGISTRATION,
                appUser);
        userTokenService.save(confirmationToken);
        log.info("Saving token: " + token + " for user: " + appUser.getEmail());
        String link = registrationTemplateConfig.getUrl() + token;
        emailService.sendRegistrationEmail(appUser, link);

    }

    private void validateCreateUserRequest(CreateUserRequest createUserRequest) {
        if(createUserRequest.getEmail() == null || createUserRequest.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if(createUserRequest.getPassword() == null || createUserRequest.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (createUserRequest.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if(createUserRequest.getFirstName() == null || createUserRequest.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if(createUserRequest.getLastName() == null || createUserRequest.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(createUserRequest.getEmail());
        if(!m.matches()) {
            throw new IllegalArgumentException("Email is not valid");
        }
    }

    /**
     * This method retrieves a user by email
     * @param username email of user
     * @return found user
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("user with username %s not found", username)));
    }

    /**
     * This method changes verified status of user and activates it
     * @param token token to verify user
     * @return activated user
     */
    @Override
    public AppUserDTO verifyUser(String token) {
        AppUser owner = tokenService.verifyToken(token);
        owner.setEnabled(true);
       return appUserMapper.appUserToAppUserDTO(userRepository.save(owner));
    }

    /**
     * This method returns a currently logged in user
     * @return currently logged in user
     */
    @Override
    public AppUser getCurrentUser() {
        // get user from security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("User with email {} is logged in", auth.getName());
        return userRepository.findByEmail(auth.getName()).orElseThrow(() -> new UsernameNotFoundException("User " + auth.getName() + " not found"));
    }

    /**
     * This method saves a user to database
     * @param appUser user to save
     * @return saved user
     */

    @Override
    public AppUser save(AppUser appUser) {
        return userRepository.save(appUser);
    }

    /**
     * This method returns a page of users
     * @param fulltext fulltext search string
     * @param role role of user
     * @param page page number
     * @param size size of page
     * @return page of users
     */
    @Override
    public UsersPage getAll(String fulltext, String role, Integer page, Integer size) {
        log.info("Getting page {} of size {}", page, size);
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size < 1) {
            size = Integer.MAX_VALUE;
        }

        Specification<AppUser> specification = (root, query, crieriaBuilder) -> {
            if (fulltext != null && !fulltext.isEmpty()) {
                return crieriaBuilder.or(
                        crieriaBuilder.like(root.get("firstName"), "%" + fulltext + "%"),
                        crieriaBuilder.like(root.get("lastName"), "%" + fulltext + "%"),
                        crieriaBuilder.like(root.get("email"), "%" + fulltext + "%")
                );
            }
            return crieriaBuilder.conjunction();
        };

        if (role != null && !role.isEmpty()) {
            specification = specification.and((root, query, crieriaBuilder) -> crieriaBuilder.equal(root.get("role"), UserRole.valueOf(role)));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Page<AppUser> userPage = userRepository.findAll(specification, pageable);
        return pageMapper.toUsersPage(userPage);
    }

    /**
     * This method updates user's balance
     * @param userId id of user
     * @param balance new balance
     * @return updated user
     */
    @Override
    public AppUserDTO updateUserBalance(String userId, Integer balance) {
        if (balance == null || balance < 0) {
            throw new IllegalArgumentException("Balance cannot be null or negative");
        }
        AppUser user = getUserById(userId);
        user.setBalance(balance);
        log.info("User {} updated balance to {}", user.getEmail(), balance);
        return appUserMapper.appUserToAppUserDTO(userRepository.save(user));
    }

    /**
     * This method updates user's role
     * @param userId id of user
     * @param role new role
     * @return updated user
     */
    @Override
    public AppUserDTO updateUserRole(String userId, UpdateUserRoleRequest.RoleEnum role) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User id cannot be null or empty");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        UserRole newRole = UserRole.valueOf(role.getValue());
        if (newRole == UserRole.USER) {
            List<AppUser> admins = userRepository.findAllByRole(UserRole.ADMIN);
            if (admins.size() == 1 && admins.get(0).getId().equals(userId)) {
                throw new LastAdminException("Cannot remove the last admin");
            }
        }
        AppUser appUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        appUser.setRole(newRole);
        return appUserMapper.appUserToAppUserDTO(userRepository.save(appUser));
    }

    /**
     * This method changes user's password
     * @param userId id of user
     * @param changePasswordRequest request containing old and new password
     * @return updated user
     */
    @Override
    public AppUserDTO changePassword(String userId, ChangePasswordRequest changePasswordRequest) {
        if (!getCurrentUser().getId().equals(userId)) {
            throw new ResourceNotOwnedException("Cannot change password of another user");
        }
        if (!bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(), getCurrentUser().getPassword())) {
            throw new PasswordMissmatchException("Old password is incorrect");
        }
        if (changePasswordRequest.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        AppUser appUser = getCurrentUser();
        appUser.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.getNewPassword()));
        log.info("User {} changed password", appUser.getEmail());
        return appUserMapper.appUserToAppUserDTO(userRepository.save(appUser));
    }

    /**
     * This method changes user's discount entitlement
     * @param userId id of user
     * @param hasDiscountEntitlement new discount entitlement
     * @return updated user
     */
    @Override
    public AppUserDTO updateDiscountEntitlement(String userId, boolean hasDiscountEntitlement) {
        AppUser currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId) && currentUser.getRole() != UserRole.ADMIN) {
            throw new ResourceNotOwnedException("Cannot change discount entitlement of another user");
        }
        AppUser appUser = getUserById(userId);
        appUser.setHasDailyDiscount(hasDiscountEntitlement);
        log.info("User {} updated discount entitlement to {}", appUser.getEmail(), hasDiscountEntitlement);
        return appUserMapper.appUserToAppUserDTO(userRepository.save(appUser));
    }
}
