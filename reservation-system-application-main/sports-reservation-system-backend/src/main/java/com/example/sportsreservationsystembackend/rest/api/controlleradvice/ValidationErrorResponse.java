package com.example.sportsreservationsystembackend.rest.api.controlleradvice;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents validation error response
 * It is used to send validation errors to the client
 *
 * @author Radim Stejskal
 */

@Data
public class ValidationErrorResponse {

    private List<Violation> violations = new ArrayList<>();
}
