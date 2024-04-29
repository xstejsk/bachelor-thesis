package com.example.sportsreservationsystembackend.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class represents reservation template config
 *
 * @Author Radim Stejskal
 */
@Configuration
@ConfigurationProperties(prefix = "email.templates.reservation-confirmation")
public class ReservationTemplateConfig extends TemplateConfig {
}
