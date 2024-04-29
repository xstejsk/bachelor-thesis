package com.example.sportsreservationsystembackend.service.impl;

import com.example.sportsreservationsystembackend.service.config.RegistrationTemplateConfig;
import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.Event;
import com.example.sportsreservationsystembackend.service.NotificationService;
import com.example.sportsreservationsystembackend.service.config.ReservationDeletionTemplateConfig;
import com.example.sportsreservationsystembackend.service.config.ReservationTemplateConfig;
import com.example.sportsreservationsystembackend.service.config.ResetPasswordTemplateConfig;
import com.example.sportsreservationsystembackend.service.config.TemplateConfig;
import com.example.sportsreservationsystembackend.utils.EmailFormatter;
import com.example.sportsreservationsystembackend.utils.NotificationsUtil;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * This class represents notification service implementation
 *
 * @Author Radim Stejskal
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;
    private final RegistrationTemplateConfig registrationTemplateConfig;
    private final ReservationTemplateConfig reservationTemplateConfig;
    private final ReservationDeletionTemplateConfig reservationDeletionTemplateConfig;
    private final ResetPasswordTemplateConfig resetPasswordTemplateConfig;

    private String sender = "myjavatenniscourts@gmail.com";

    /**
     * This method sends email with new password to user
     * @param recipient user who requested password reset
     * @param token token for password reset
     * @param password new unencrypted password
     */
    @Override
    public void sendResetPasswordEmail(AppUser recipient, String token, String password) {
        EmailFormatter emailFormatter = new EmailFormatter.Builder()
                .recipientName(NotificationsUtil.vocative(recipient.getFirstName()))
                .link(resetPasswordTemplateConfig.getUrl() + token)
                .linkExpiration(registrationTemplateConfig.getExpirationMinutes())
                .password(password)
                .build();
        String body = emailFormatter.formatEmail(getTemplate(resetPasswordTemplateConfig.getPath()));
        sendEmailAsync(recipient.getEmail(), body, resetPasswordTemplateConfig.getSubject());
    }

    /**
     * This method sends email with link for registration
     * @param recipient user who requested registration
     * @param link link for registration
     */
    @Override
    public void sendRegistrationEmail(AppUser recipient, String link) {
        EmailFormatter emailFormatter = new EmailFormatter.Builder()
                .recipientName(NotificationsUtil.vocative(recipient.getFirstName()))
                .link(link)
                .linkExpiration(registrationTemplateConfig.getExpirationMinutes())
                .build();
        String body = emailFormatter.formatEmail(getTemplate(registrationTemplateConfig.getPath()));
        sendEmailAsync(recipient.getEmail(), body, registrationTemplateConfig.getSubject());
        log.info("Email sent to: {} with link {}" + recipient.getEmail(), link);
    }

    /**
     * This method sends email with reservation information
     * @param recipient user who created reservation
     * @param event event for which reservation was created
     */
    @Override
    public void sendNewReservationEmail(AppUser recipient, Event event) {
        sendReservationEmail(recipient, event, reservationTemplateConfig);
    }

    /**
     * This method sends email confirming reservation deletion
     * @param recipient user who deleted reservation
     * @param event event for which reservation was deleted
     */
    @Override
    public void sendReservationCancelledEmail(AppUser recipient, Event event) {
        sendReservationEmail(recipient, event, reservationDeletionTemplateConfig);
    }

    private void sendReservationEmail(AppUser recipient, Event event, TemplateConfig templateConfig) {
        EmailFormatter emailFormatter = new EmailFormatter.Builder()
                .recipientName(NotificationsUtil.vocative(recipient.getFirstName()))
                .eventName(event.getTitle())
                .eventDate(event.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .eventTime(event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .locationName(event.getCalendar().getLocation().getName())
                .build();
        String body = emailFormatter.formatEmail(getTemplate(templateConfig.getPath()));
        sendEmailAsync(recipient.getEmail(), body, templateConfig.getSubject());
    }

    private void sendEmailAsync(String to, String body, String subject) {
        new Thread(() -> {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setText(body, true);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setFrom(sender);
                mailSender.send(mimeMessage);
                log.info("Email sent to: " + to);
            }catch (Exception e){
                log.error("failed to send email", e);
                throw new IllegalStateException("failed to send email");
            }
        }).start();
    }

    private String getTemplate(String path) {
        log.info("Getting template from: " + path);
        return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
    }
}
