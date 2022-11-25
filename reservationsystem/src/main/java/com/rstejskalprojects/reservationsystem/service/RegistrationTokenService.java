package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.RegistrationToken;
import com.rstejskalprojects.reservationsystem.repository.RegistrationTokenRepository;
import com.rstejskalprojects.reservationsystem.util.customexception.UnknownRegistrationTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationTokenService {

    private final RegistrationTokenRepository registrationTokenRepository;

    public void saveRegistrationToken(RegistrationToken registrationToken){
        registrationTokenRepository.save(registrationToken);
    }

    public Optional<RegistrationToken> getToken(String token) throws UnknownRegistrationTokenException {
        boolean exists = registrationTokenRepository.findByToken(token).isPresent();
        if (!exists){
            throw new UnknownRegistrationTokenException("given registration token does not exist");
        }
        return registrationTokenRepository.findByToken(token);
    }

    public void setConfirmedAt(String token) {
        registrationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}
