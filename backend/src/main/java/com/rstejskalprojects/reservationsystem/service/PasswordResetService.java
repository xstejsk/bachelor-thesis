package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.UserToken;

public interface PasswordResetService {

    void confirmToken(UserToken token);

    void sendResetPasswordEmail(String email);
}
