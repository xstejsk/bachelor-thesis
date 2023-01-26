package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.PasswordToken;

public interface PasswordResetService {

    void confirmToken(PasswordToken token);

    void sendResetPasswordEmail(String email);
}
