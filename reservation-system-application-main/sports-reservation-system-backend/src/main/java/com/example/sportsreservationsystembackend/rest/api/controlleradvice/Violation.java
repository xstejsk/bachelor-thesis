package com.example.sportsreservationsystembackend.rest.api.controlleradvice;

import lombok.Data;

/**
 * This class represents a constraint validation
 *
 * @author Radim Stejskal
 */

@Data
public class Violation {

    private final String fieldName;
    private final String message;
}

