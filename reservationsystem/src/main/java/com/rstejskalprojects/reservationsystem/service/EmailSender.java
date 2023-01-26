package com.rstejskalprojects.reservationsystem.service;

public interface EmailSender {
    void sendEmail(String to, String email, String subject);
}
