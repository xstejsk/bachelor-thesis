package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.api.controller.model.UpdateEventRequest;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;

import java.util.List;

public interface EventService {
    List<Event> findAll();

    List<Event> saveEvent(Event event);

    List<Event> saveEvent(EventDTO eventDTO);

    List<Event> findByLocationId(Long id);

    void deleteEvent(Long id);

    void deleteRecurrentEvents(Long groupId);

    List<Event> findOverlappingEvents(Event event);

    Event updateEvent(Long eventId, UpdateEventRequest updateEventRequest);

    List<Event> updateRecurrentEvents(Long recurrenceGroupId, UpdateEventRequest updateEventRequest);
}
