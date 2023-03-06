package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.NonPersistentPasswordToken;
import com.rstejskalprojects.reservationsystem.model.PasswordToken;
import com.rstejskalprojects.reservationsystem.util.customexception.ExpiredTokenException;
import com.rstejskalprojects.reservationsystem.util.customexception.UsedTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final TokenService<PasswordToken> passwordTokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailSender emailSender;
    @Value("${host.domain}")
    private String host;
    private final static int PASSWORD_LENGTH = 8;

    @Override
    public void sendResetPasswordEmail(String email) {
        try {
            AppUser appUser = (AppUser) userDetailsService.loadUserByUsername(email);
            NonPersistentPasswordToken token = createPasswordResetTokenForUser(appUser);
            String link = host + "/password-reset/confirm?token=" + token.getToken();
            new Thread(() -> emailSender.sendEmail(email, buildEmail(appUser.getFirstName(), link, token.getPassword()), "Reset hesla")).start();
            log.info("Email with password reset link was sent to " + email);
        } catch (UsernameNotFoundException e) {
            log.warn("User {} not found", email);
            throw new UsernameNotFoundException("user with given email does not exist");
        }
    }

    private NonPersistentPasswordToken createPasswordResetTokenForUser(AppUser appUser) {
        String token = UUID.randomUUID().toString();
        String password = generateRandomPassword(PASSWORD_LENGTH);
        PasswordToken passwordToken = new PasswordToken(token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                bCryptPasswordEncoder.encode(password),
                appUser);
        passwordTokenService.saveToken(passwordToken);
        return new NonPersistentPasswordToken(passwordToken.getToken(), passwordToken.getUser(), password);
    }

    @Override
    public void confirmToken(PasswordToken passwordToken) {
        if (passwordToken.getConfirmedAt() != null) {
            throw new UsedTokenException("Password was already set");
        }

        LocalDateTime expiredAt = passwordToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException("Token has expired");
        }
        passwordTokenService.setConfirmedAt(passwordToken);
        AppUser appUser = passwordToken.getUser();
        String encodedPassword = passwordToken.getEncodedPassword();
        userDetailsService.changeUserEncodedPassword(appUser, encodedPassword);
        log.info("Password for user " + appUser.getUsername() + " was changed");
    }



    public static String generateRandomPassword(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi"
                +"jklmnopqrstuvwxyz";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    private String buildEmail(String name, String link, String password) {
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
                "inline-block\">Resetujte svoje heslo</span>\n" +
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
                "line-height:25px;color:#0b0c0c\"> Obdrželi jsme žádost o reset hesla k vašemu účtu." +
                " Pokud jste ji neodeslali vy, ignorujte tento email. Pokud jste ji odeslali vy," +
                " klikněte na následující odkaz, čímž potvrdíte nové heslo." +
                "<p>Vaše nové heslo je: " + password + "</p>" +
                " </p><blockquote style=" +
                "\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;" +
                "font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;" +
                "line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Potvrdit změnu hesla</a> " +
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
