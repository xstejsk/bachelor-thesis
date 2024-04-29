package com.example.sportsreservationsystembackend.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class represents reset password template config
 *
 * @Author Radim Stejskal
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "email.templates.reset-password")
public class ResetPasswordTemplateConfig extends TemplateConfig {

    private String url;
    private String expirationMinutes;
}
