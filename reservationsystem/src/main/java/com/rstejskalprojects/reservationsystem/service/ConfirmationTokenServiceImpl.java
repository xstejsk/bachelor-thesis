package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.RegistrationToken;
import com.rstejskalprojects.reservationsystem.repository.RegistrationTokenRepository;
import com.rstejskalprojects.reservationsystem.util.customexception.UnknownTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements TokenService<RegistrationToken> {

    private final RegistrationTokenRepository registrationTokenRepository;

    public void saveToken(RegistrationToken registrationToken){
        registrationTokenRepository.save(registrationToken);
    }

    public RegistrationToken getToken(String token) throws UnknownTokenException {
        boolean exists = registrationTokenRepository.findByToken(token).isPresent();
        if (!exists){
            throw new UnknownTokenException("given registration token does not exist");
        }
        return registrationTokenRepository.findByToken(token).get();
    }

    public void setConfirmedAt(RegistrationToken registrationToken) {
        registrationTokenRepository.updateConfirmedAt(registrationToken.getToken(), LocalDateTime.now());
    }
}
