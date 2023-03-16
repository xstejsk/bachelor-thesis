package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.dto.EmailBodyPlaceholdersEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Slf4j
@RequiredArgsConstructor
@ComponentScan("com.rstejskalprojects.reservationsystem")
public class EmailFormatterServiceImpl implements EmailFormatterService {

    @Value("${emailTemplates.registrationConfirmation.path}")
    private String registrationTemplatePath;
    @Value("${emailTemplates.cancelReservation.path}")
    private String reservationCancellationTemplatePath;
    @Value("${emailTemplates.confirmReservation.path}")
    private String reservationConfirmationTemplatePath;
    @Value("${emailTemplates.passwordReset.path}")
    private String passwordResetTemplatePath;
    @Value("${emailTemplates.cancelEvent.path}")
    private String eventCancellationTemplatePath;
    @Value("${emailTemplates.cancelRecurrentEvent.path}")
    private String recurrentEventCancellationTemplatePath;

    private String formatEmail(String body, String recipient, String link,
                               Integer linkExpirationMinutes, String eventName, String eventTime, String locationName, String password) {
        body = body.replace(EmailBodyPlaceholdersEnum.RECIPIENT.getValue(), recipient);
        body = body.replace(EmailBodyPlaceholdersEnum.LINK.getValue(), link);
        body = body.replace(EmailBodyPlaceholdersEnum.LINK_EXPIRATION.getValue(), linkExpirationMinutes.toString());
        body = body.replace(EmailBodyPlaceholdersEnum.EVENT_NAME.getValue(), eventName);
        body = body.replace(EmailBodyPlaceholdersEnum.PASSWORD.getValue(), password);
        body = body.replace(EmailBodyPlaceholdersEnum.EVENT_TIME.getValue(), eventTime);
        body = body.replace(EmailBodyPlaceholdersEnum.LOCATION_NAME.getValue(), locationName);
        return body;
    }

    @Override
    public String formatReservationConfirmationEmail(String recipient, String eventName, String locationName, String eventTime) {
        try {
            Resource resource = new ClassPathResource(reservationConfirmationTemplatePath);
            byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));
            String body = new String(bytes);
            return formatEmail(body, recipient, "", 0, eventName, eventTime, locationName, "");
        } catch (IOException e) {
            log.error("Error while reading email template", e);
            throw new RuntimeException("Error while reading email template");
        }

    }

    @Override
    public String formatReservationCancellationEmail(String recipient, String eventName, String locationName, String eventTime) {
        try {
            Resource resource = new ClassPathResource(reservationCancellationTemplatePath);
            byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));
            String body = new String(bytes);
            return formatEmail(body, recipient, "", 0, eventName, eventTime, locationName, "");
        } catch (IOException e) {
            log.error("Error while reading email template", e);
            throw new RuntimeException("Error while reading email template");
        }
    }

    @Override
    public String formatEventCancellationEmail(String recipient, String eventName, String locationName, String eventTime) {
        try {
            Resource resource = new ClassPathResource(eventCancellationTemplatePath);
            byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));
            String body = new String(bytes);
            return formatEmail(body, recipient, "", 0, eventName, eventTime, locationName, "");
        } catch (IOException e) {
            log.error("Error while reading email template", e);
            throw new RuntimeException("Error while reading email template");
        }
    }

    @Override
    public String formatRegistrationConfirmationEmail(String recipient, String link, Integer linkExpirationMinutes) {
        try {
            Resource resource = new ClassPathResource(registrationTemplatePath);
            byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));
            String body = new String(bytes);
            return formatEmail(body, recipient, link, linkExpirationMinutes, "", "", "", "");
        } catch (IOException e) {
            log.error("Error while reading email template", e);
            throw new RuntimeException("Error while reading email template");
        }
    }

    @Override
    public String formatPasswordResetEmail(String recipient, String link, Integer linkExpirationMinutes, String password) {
        try {
            Resource resource = new ClassPathResource(passwordResetTemplatePath);
            byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));
            String body = new String(bytes);
            return formatEmail(body, recipient, link, linkExpirationMinutes, "", "", "", password);
        } catch (IOException e) {
            log.error("Error while reading email template", e);
            throw new RuntimeException("Error while reading email template");
        }
    }

    @Override
    public String formatRecurrentEventCancellationEmail(String recipient, String eventName) {
        try {
            Resource resource = new ClassPathResource(recurrentEventCancellationTemplatePath);
            byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));
            String body = new String(bytes);
            return formatEmail(body, recipient, "", 0, eventName, "", "", "");
        } catch (IOException e) {
            log.error("Error while reading email template", e);
            throw new RuntimeException("Error while reading email template");
        }
    }


}
