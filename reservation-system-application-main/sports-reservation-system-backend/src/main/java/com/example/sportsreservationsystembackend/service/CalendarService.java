package com.example.sportsreservationsystembackend.service;

import com.example.sportsreservationsystembackend.model.Calendar;
import com.xstejsk.reservationapp.main.rest.model.CalendarDTO;
import com.xstejsk.reservationapp.main.rest.model.CalendarWithEventsDTO;
import com.xstejsk.reservationapp.main.rest.model.CalendarsPage;
import com.xstejsk.reservationapp.main.rest.model.CreateCalendarRequest;
import com.xstejsk.reservationapp.main.rest.model.UpdateCalendarRequest;

import java.time.LocalDate;

public interface CalendarService {

    CalendarDTO create(CreateCalendarRequest createCalendarRequest);

    void delete(String calendarId);

    CalendarsPage getAll(Integer page, Integer size, String fulltext);

    CalendarWithEventsDTO getById(String calendarId, LocalDate eventsFrom, LocalDate eventsTo);

    Calendar getEntityById(String calendarId);

    CalendarDTO update(String calendarId, UpdateCalendarRequest updateCalendarRequest);

    boolean exists(String calendarId);
}
