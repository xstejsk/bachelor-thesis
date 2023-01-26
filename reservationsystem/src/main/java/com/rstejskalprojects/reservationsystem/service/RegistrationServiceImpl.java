package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.api.model.authorization.RegistrationRequest;
import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.model.RegistrationToken;
import com.rstejskalprojects.reservationsystem.util.customexception.ExpiredTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UnknownTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UsedTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    private final UserDetailsServiceImpl userDetailsService;
    private final ConfirmationTokenServiceImpl registrationTokenService;
    private final EmailSender emailSender;
    @Value("${host.domain}")
    private String host;

    public String register(RegistrationRequest request){
        String token = userDetailsService.saveUser(
                new AppUser(request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        UserRoleEnum.USER));

        String link = host + "/registration/confirm/?token=" + token;
        new Thread(() -> emailSender.sendEmail(request.getEmail(), buildEmail(request.getFirstName(), link), "Potvrzení registrace")).start();
        return token;
    }

    @Override
    public String resendRegistrationEmail(String email) {
        try {
            AppUser appUser = (AppUser) userDetailsService.loadUserByUsername(email);
            String token = UUID.randomUUID().toString();
            RegistrationToken confirmationToken = new RegistrationToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(30),
                    appUser);

            registrationTokenService.saveToken(confirmationToken);
            String link = host + "/registration/confirm?token=" + token;
            new Thread(() -> emailSender.sendEmail(email, buildEmail(appUser.getFirstName(), link), "Potvrzení registrace")).start();
            log.info("Registration token for user {} was resent", email);
            return token;
        } catch (UsernameNotFoundException e) {
            log.warn("User {} not found", email);
            throw new UsernameNotFoundException("user with given email does not exist");
        }
    }

    @Transactional
    public void confirmToken(String token) throws UnknownTokenException {
        RegistrationToken confirmationToken = registrationTokenService
                .getToken(token);

        if (confirmationToken.getConfirmedAt() != null) {
            throw new UsedTokenException("Email is already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException("Token has expired");
        }

        registrationTokenService.setConfirmedAt(confirmationToken);
        userDetailsService.enableUser(confirmationToken.getUser().getEmail());
        log.info("User {} was enabled", confirmationToken.getUser().getEmail());
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;" +
                "color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"" +
                "border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\"" +
                " cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"" +
                "border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\"" +
                " border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                 \n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;" +
                "Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;" +
                "font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:" +
                "inline-block\">Potvrďte vaši emailovou adresu</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" " +
                "align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=" +
                "\"border-collapse:collapse;max-width:580px;width:100%!important\"" +
                " width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" " +
                "cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" " +
                "align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" " +
                "style=\"border-collapse:collapse;max-width:580px;width:100%!important\" " +
                "width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;" +
                "line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;" +
                "color:#0b0c0c\">Dobrý den " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;" +
                "line-height:25px;color:#0b0c0c\"> Děkujeme za Vaši registraci." +
                " Pro aktivaci účtu, klikněte prosím na odkaz níže: </p><blockquote style=" +
                "\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;" +
                "font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;" +
                "line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Aktivovat</a> " +
                "</p></blockquote>\n Platnost odkazu vyprší za 30 minut." +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}