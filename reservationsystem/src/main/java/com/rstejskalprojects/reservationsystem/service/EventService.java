package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Event;

import java.util.List;

public interface EventService {
    Event findEventById(Long id);

    List<Event> findAll();
}
