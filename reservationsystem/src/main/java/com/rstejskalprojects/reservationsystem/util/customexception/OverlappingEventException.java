package com.rstejskalprojects.reservationsystem.util.customexception;

import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;

import java.util.List;

public class OverlappingEventException extends RuntimeException {
    List<EventDTO> overlappingEvents;

    public OverlappingEventException() {
    }

    public OverlappingEventException(String message, List<EventDTO> overlappingEvents) {
        super(message);
        this.overlappingEvents = overlappingEvents;
    }

    public List<EventDTO> getOverlappingEvents() {
        return overlappingEvents;
    }

    public void setOverlappingEvents(List<EventDTO> overlappingEvents) {
        this.overlappingEvents = overlappingEvents;
    }
}
