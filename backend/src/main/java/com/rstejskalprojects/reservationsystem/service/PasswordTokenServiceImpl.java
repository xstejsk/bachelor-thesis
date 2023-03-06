package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.PasswordToken;
import com.rstejskalprojects.reservationsystem.repository.PasswordTokenRespository;
import com.rstejskalprojects.reservationsystem.util.customexception.UnknownTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordTokenServiceImpl implements TokenService<PasswordToken> {

    private final PasswordTokenRespository passwordTokenRespository;

    @Override
    public void saveToken(PasswordToken passwordToken) {
        passwordTokenRespository.save(passwordToken);
    }

    @Override
    public PasswordToken getToken(String token) throws UnknownTokenException {
        boolean exists = passwordTokenRespository.findByToken(token).isPresent();
        if (!exists){
            throw new UnknownTokenException("given password token does not exist");
        }
        return passwordTokenRespository.findByToken(token).get();
    }

    public void setConfirmedAt(PasswordToken token) {
        passwordTokenRespository.updateConfirmedAt(token.getToken(), LocalDateTime.now());
    }
}
