package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.TokenTypeEnum;
import com.rstejskalprojects.reservationsystem.model.UserToken;
import com.rstejskalprojects.reservationsystem.repository.UserTokenRepository;
import com.rstejskalprojects.reservationsystem.util.customexception.UnknownTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserTokenService implements TokenService<UserToken> {

    private final UserTokenRepository userTokenRepository;
    private final PasswordResetService passwordResetService;
    private final RegistrationService registrationService;

    @Override
    public void saveToken(UserToken userToken) {
        userTokenRepository.save(userToken);
    }

    @Override
    public UserToken getToken(String token) throws UnknownTokenException {
        boolean exists = userTokenRepository.findByToken(token).isPresent();
        if (!exists){
            throw new UnknownTokenException("given password token does not exist");
        }
        return userTokenRepository.findByToken(token).get();
    }

    public void confirmToken(UserToken token) {
        if (token.getTokenType() == TokenTypeEnum.PASSWORD_RESET) {
            passwordResetService.confirmToken(token);
        } else if (token.getTokenType() == TokenTypeEnum.REGISTRATION) {
            registrationService.confirmToken(token);
        } else {
            throw new IllegalArgumentException("Token type is not supported");
        }
    }
}
