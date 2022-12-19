package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;

import java.util.List;

public interface EventService {
    Event findEventById(Long id);

    List<Event> findAll();

    List<Event> saveEvent(Event event);

    List<Event> saveEvent(EventDTO eventDTO);

    List<Event> findByLocationName(String name);

    List<Event> findByLocationId(Long id);
}
