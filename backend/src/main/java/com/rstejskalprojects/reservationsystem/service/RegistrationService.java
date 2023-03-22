package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.api.model.authorization.RegistrationRequest;
import com.rstejskalprojects.reservationsystem.model.UserToken;

public interface RegistrationService {
    String register(RegistrationRequest request);

    void confirmToken(UserToken token);

    void resendRegistrationEmail(String email);
}
