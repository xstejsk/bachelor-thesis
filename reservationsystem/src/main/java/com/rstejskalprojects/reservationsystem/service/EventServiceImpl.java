package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;


    @Override
    public Event findEventById(Long id) {
        return null;
    }

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }
}
