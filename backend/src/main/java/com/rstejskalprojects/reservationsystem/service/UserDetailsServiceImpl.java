package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.PasswordToken;
import com.rstejskalprojects.reservationsystem.model.RegistrationToken;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.repository.UserRepository;
import com.rstejskalprojects.reservationsystem.util.customexception.ReservationNotFoundException;
import com.rstejskalprojects.reservationsystem.util.customexception.UserIdNotFoundException;
import com.rstejskalprojects.reservationsystem.util.customexception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ConfirmationTokenServiceImpl registrationTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("user with username %s not found", username)));
    }

    public boolean existsByUsername(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }

    public List<AppUser> findAll() {
        return userRepository.findAll();
    }

    public AppUser findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserIdNotFoundException(String.format("user with id %s not found", id)));
    }

    public void changeUserPassword(AppUser appUser, String password) {
        appUser.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(appUser);
    }

    public void changeUserEncodedPassword(AppUser appUser, String encodedPassword) {
        appUser.setPassword(encodedPassword);
        userRepository.save(appUser);
    }

    public String saveUser(AppUser appUser){
        Optional<AppUser> newUser = userRepository.findUserByUsername(appUser.getEmail());
        if (newUser.isPresent() && newUser.get().getEnabled()){
            throw new IllegalStateException("account with this email is already registered");
        }else if (newUser.isPresent() && !newUser.get().getEnabled()){
            appUser = newUser.get();
        }
        else if (newUser.isEmpty()){
            userRepository.save(appUser);
        }
        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        userRepository.save(appUser);

        String token = UUID.randomUUID().toString();
        RegistrationToken confirmationToken = new RegistrationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                appUser);

        registrationTokenService.saveToken(confirmationToken);
        log.info("new user registered with email: " + appUser.getEmail());
        return token;
    }

    public void enableUser(String email) {
        AppUser appUser = userRepository.findUserByUsername(email).orElseThrow(() -> new ReservationNotFoundException(String.format("user with email %s not found", email)));
        userRepository.enableUser(email);
    }

    public void promoteUser(Long userId) {
        AppUser appUser = userRepository.findById(userId).orElseThrow(() ->
                new ReservationNotFoundException(String.format("user with id %s not found", userId)));
        appUser.setUserRole(UserRoleEnum.ADMIN);
        appUser.setLocked(false);
        userRepository.save(appUser);
        log.info("user with id: " + userId + " promoted to admin");
    }

    public void blockUser(Long userId) {
        AppUser appUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("user with id %s not found", userId)));
        if (appUser.getUserRole().equals(UserRoleEnum.ADMIN)){
            throw new IllegalStateException("admin cannot be blocked");
        }
        appUser.setLocked(true);
        userRepository.save(appUser);
        log.info("user with id: " + userId + " has been blocked");
    }

    public void unblockUser(Long userId) {
        AppUser appUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("user with id %s not found", userId)));
        appUser.setLocked(false);
        userRepository.save(appUser);
        log.info("user with id: " + userId + " has been unblocked");
    }

    public Optional<AppUser> loadUserById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long userId) {
        AppUser appUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("user with id %s not found", userId)));
        if (appUser.getUserRole().equals(UserRoleEnum.ADMIN)){
            throw new IllegalStateException("admin cannot be deleted");
        }
        userRepository.delete(appUser);
        log.info("user with id: " + userId + " has been deleted");
    }

}
