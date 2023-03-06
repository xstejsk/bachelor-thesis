package com.rstejskalprojects.reservationsystem.util.customexception;

import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;

import java.util.List;

public class OverlappingEventException extends RuntimeException {

    public OverlappingEventException() {
    }

    public OverlappingEventException(String message) {
        super(message);
    }

}
