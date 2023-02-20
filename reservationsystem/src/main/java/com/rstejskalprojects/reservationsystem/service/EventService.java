package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.api.controller.model.UpdateEventRequest;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;

import java.util.List;

public interface EventService {
    Event findEventById(Long id);

    List<Event> findAll();

    List<Event> saveEvent(Event event);

    List<Event> saveEvent(EventDTO eventDTO);

    List<Event> findByLocationId(Long id);

    List<Event> findActiveByLocationId(Long id);

    List<Event> findByLocationName(String name);

    Event cancelEvent(EventDTO eventDTO);

    Event cancelEvent(Event event);

    List<Event> cancelRecurrentEvents(Long groupId);

    List<Event> findAllNonCanceled();

    List<Event> findOverlappingEvents(Event event);

    Event updateEvent(Long eventId, UpdateEventRequest updateEventRequest);

    List<Event> updateRecurrentEvents(Long recurrenceGroupId, UpdateEventRequest updateEventRequest);
}
