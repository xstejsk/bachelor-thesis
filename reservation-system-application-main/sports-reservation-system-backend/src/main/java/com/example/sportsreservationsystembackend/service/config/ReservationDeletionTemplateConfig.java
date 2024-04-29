package com.example.sportsreservationsystembackend.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class represents reservation deletion template config
 *
 * @author Radim Stejskal
 */

@Configuration
@ConfigurationProperties(prefix = "email.templates.reservation-deletion")
public class ReservationDeletionTemplateConfig extends TemplateConfig {
}
