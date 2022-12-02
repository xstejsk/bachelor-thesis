package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;

import java.util.List;

public interface EventService {
    Event findEventById(Long id);

    List<Event> findAll();

    Event saveEvent(Event event);

    Event saveEvent(EventDTO eventDTO);
}
