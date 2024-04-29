package com.example.sportsreservationsystembackend.service;

import com.example.sportsreservationsystembackend.model.Event;
import com.xstejsk.reservationapp.main.rest.model.CreateEventRequest;
import com.xstejsk.reservationapp.main.rest.model.EventDTO;
import com.xstejsk.reservationapp.main.rest.model.EventsPage;
import com.xstejsk.reservationapp.main.rest.model.UpdateEventRequest;

import java.time.LocalDate;
import java.util.List;

public interface EventService {

    List<Event> getEventsByCalendarId(String calendarId, LocalDate from, LocalDate to);

    List<EventDTO> createEvent(String calendarId, CreateEventRequest createEventRequest);

    EventDTO deleteEvent(String eventId);

    Event getEventById(String calendarId, String eventId);

    List<EventDTO> updateEvent(String eventId, UpdateEventRequest updateEventRequest);

    EventsPage getAll(LocalDate from, String calendarId, Integer page, Integer size);

    boolean calendarContainsFutureEvents(String calendarId);
}
