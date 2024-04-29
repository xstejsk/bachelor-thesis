package com.example.sportsreservationsystembackend.service.config;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;

/**
 * This class represents a generic template config
 *
 * @Author Radim Stejskal
 */
@Getter
@Setter
public abstract class TemplateConfig {

    protected String subject;
    protected String path;

    @PostConstruct
    public void setFileSeparator() {
        path = path.replace("{fileSeparator}", System.getProperty("file.separator"));
    }
}
