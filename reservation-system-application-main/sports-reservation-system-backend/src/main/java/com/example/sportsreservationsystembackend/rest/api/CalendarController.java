package com.example.sportsreservationsystembackend.rest.api;

import com.example.sportsreservationsystembackend.service.CalendarService;
import com.example.sportsreservationsystembackend.service.EventService;
import com.example.sportsreservationsystembackend.service.ReservationService;
import com.xstejsk.reservationapp.main.rest.api.CalendarsApi;
import com.xstejsk.reservationapp.main.rest.model.CalendarDTO;
import com.xstejsk.reservationapp.main.rest.model.CalendarWithEventsDTO;
import com.xstejsk.reservationapp.main.rest.model.CalendarsPage;
import com.xstejsk.reservationapp.main.rest.model.CreateCalendarRequest;
import com.xstejsk.reservationapp.main.rest.model.CreateEventRequest;
import com.xstejsk.reservationapp.main.rest.model.EventDTO;
import com.xstejsk.reservationapp.main.rest.model.ReservationDTO;
import com.xstejsk.reservationapp.main.rest.model.UpdateCalendarRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * This class represents calendar controller
 * It is used for calendar management
 *
 * @Author Radim Stejskal
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class CalendarController implements CalendarsApi {

    private final CalendarService calendarService;
    private final EventService eventService;
    private final ReservationService reservationService;

    /**
     * This method is used for getting creation of a calendar
     * @param createCalendarRequest  (required)
     * @return ResponseEntity<CalendarDTO> object
     */
    @Override
    public ResponseEntity<CalendarDTO> createCalendar(CreateCalendarRequest createCalendarRequest) {
        CalendarDTO calendarDTO = calendarService.create(createCalendarRequest);
        return ResponseEntity.created(null).body(calendarDTO);
    }

    /**
     * This method is used for creating an event in a calendar+
     * @param calendarId  (required)
     * @param createEventRequest  (required)
     * @return created event
     */
    @Override
    public ResponseEntity<List<EventDTO>> createEvent(String calendarId, CreateEventRequest createEventRequest) {
        log.info("Received create event request: {}", createEventRequest);
        List<EventDTO> eventDTOs = eventService.createEvent(calendarId, createEventRequest);
        return ResponseEntity.created(null).body(eventDTOs);
    }

    /**
     * This method is used for retrieving a calendar by id along with its events
     * @param calendarId  (required)
     * @param eventsFrom  date from which the events should be retrieved (optional)
     * @param eventsTo  date to which the events should be retrieved (optional)
     * @return ResponseEntity<CalendarDTO> object
     */
    @Override
    public ResponseEntity<CalendarWithEventsDTO> getCalendarById(String calendarId, LocalDate eventsFrom, LocalDate eventsTo) {
        CalendarWithEventsDTO calendarWithEventsDTOs = calendarService.getById(calendarId, eventsFrom, eventsTo);
        return ResponseEntity.ok().body(calendarWithEventsDTOs);
    }

    /**
     * This method is used for creating a reservation for an event, owner of the calendar is taken from the access token
     * @param calendarId  (required)
     * @param eventId  (required)
     * @return created reservation
     */
    @Override
    public ResponseEntity<ReservationDTO> createReservationForEvent(String calendarId, String eventId) {
        ReservationDTO reservationDTO = reservationService.create(calendarId, eventId);
        return ResponseEntity.created(null).body(reservationDTO);
    }

    /**
     * This method is used for deleting a calendar, it only deletes the calendar if it has no events
     * @param calendarId  (required)
     * @return
     */
    @Override
    public ResponseEntity<Void> deleteCalendar(String calendarId) {
        calendarService.delete(calendarId);
        return ResponseEntity.noContent().build();
    }

    /**
     * This method is used for retrieving all calendars without events
     * @param page  (optional)
     * @param size page size, default is the maximum page size (optional)
     * @param fulltext parameter that is used for fulltext search in calendar name (optional)
     * @return page of calendars
     */
    @Override
    public ResponseEntity<CalendarsPage> getAllCalendars(Integer page, Integer size, String fulltext) {
        log.info("Getting calendars with params: page: {}, size: {}, name: {}", page, size, fulltext);
        CalendarsPage calendarsPage = calendarService.getAll(page, size, fulltext);
        return ResponseEntity.ok().body(calendarsPage);
    }

    /**
     * This method is used for updating a calendar details
     * @param calendarId  (required)
     * @param updateCalendarRequest  (required)
     * @return updated calendar
     */
    @Override
    public ResponseEntity<CalendarDTO> updateCalendar(String calendarId, UpdateCalendarRequest updateCalendarRequest) {
        CalendarDTO calendarDTO = calendarService.update(calendarId, updateCalendarRequest);
        return ResponseEntity.ok().body(calendarDTO);
    }
}
