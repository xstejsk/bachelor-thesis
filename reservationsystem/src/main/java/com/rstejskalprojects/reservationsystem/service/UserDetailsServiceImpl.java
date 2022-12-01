package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.RegistrationToken;
import com.rstejskalprojects.reservationsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ConfirmationTokenServiceImpl registrationTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("user with username %s not found", username)));
    }

    public List<AppUser> findAll() {
        return userRepository.findAll();
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

        registrationTokenService.saveRegistrationToken(confirmationToken);
        return token;
    }

    public void enableUser(String email) {
        userRepository.enableUser(email);
    }

    public Optional<AppUser> loadUserById(Long id) {
        return userRepository.findById(id);
    }

}
