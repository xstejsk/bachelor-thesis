package com.rstejskalprojects.reservationsystem.util.customexception;

public class EmailTemplateNotFoundException extends RuntimeException {
    public EmailTemplateNotFoundException(String message) {
        super(message);
    }
}
