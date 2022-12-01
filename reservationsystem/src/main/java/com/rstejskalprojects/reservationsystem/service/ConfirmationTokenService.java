package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.RegistrationToken;

import java.util.Optional;

public interface ConfirmationTokenService {
    void saveRegistrationToken(RegistrationToken registrationToken);

    Optional<RegistrationToken> getToken(String token);
}
