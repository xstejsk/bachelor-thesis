package com.rstejskalprojects.reservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {

    private final static Logger logger = LoggerFactory.getLogger(EmailSenderImpl.class);
    private final JavaMailSender mailSender;

    private String sender = "myjavatenniscourts@gmail.com";

    @Override
    @Async
    public void sendEmail(String to, String email, String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply-fictonalSportsCenter@gmail.com");
            mailSender.send(mimeMessage);
        }catch (Exception e){
            logger.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
}
