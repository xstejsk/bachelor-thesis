package com.example.sportsreservationsystembackend.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class represents registration template config
 *
 * @author Radim Stejskal
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "email.templates.registration")
public class RegistrationTemplateConfig extends TemplateConfig {

    private String url;
    private String expirationMinutes;

}
