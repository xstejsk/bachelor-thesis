package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.PasswordToken;
import com.rstejskalprojects.reservationsystem.model.RegistrationToken;
import com.rstejskalprojects.reservationsystem.repository.UserRepository;
import com.rstejskalprojects.reservationsystem.util.customexception.ReservationNotFoundException;
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
    private final PasswordTokenServiceImpl passwordTokenService;

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
        return userRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException(
                String.format("Reservation with id %s not found", id)
        ));
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
        userRepository.enableUser(email);
    }

    public Optional<AppUser> loadUserById(Long id) {
        return userRepository.findById(id);
    }

}
