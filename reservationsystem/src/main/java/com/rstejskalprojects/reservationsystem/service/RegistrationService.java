package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.api.model.authorization.RegistrationRequest;

public interface RegistrationService {
    void register(RegistrationRequest request);

    void confirmToken(String token);
}
