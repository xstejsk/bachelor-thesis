package com.example.sportsreservationsystembackend.rest.api;

import com.example.sportsreservationsystembackend.service.EventService;
import com.xstejsk.reservationapp.main.rest.api.EventsApi;
import com.xstejsk.reservationapp.main.rest.model.EventDTO;
import com.xstejsk.reservationapp.main.rest.model.EventsPage;
import com.xstejsk.reservationapp.main.rest.model.UpdateEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * This class represents event controller
 *
 * @author Radim Stejskal
 */

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class EventController implements EventsApi {

    private final EventService eventService;

    /**
     * This method is used for deleting an event, event can be deleted only if it is not reserved, and it is not in the past
     * @param eventId  (required)
     * @return
     */
    @Override
    public ResponseEntity<EventDTO> deleteEvent(String eventId) {
        EventDTO eventDTO = eventService.deleteEvent(eventId);
        return ResponseEntity.ok().body(eventDTO);
    }

    /**
     * This method is used for getting all events from all calendars
     * @param from filters events which start at or after from (optional)
     * @param calendarId id of a calendar (optional)
     * @param page  (optional)
     * @param size  (optional)
     * @return
     */
    @Override
    public ResponseEntity<EventsPage> getAllEvents(LocalDate from, String calendarId, Integer page, Integer size) {
        log.info("Getting events with params: from: {}, calendarId: {}, page: {}, size: {}", from, calendarId, page, size);
        EventsPage eventsPage = eventService.getAll(from, calendarId, page, size);
        return ResponseEntity.ok().body(eventsPage);
    }

    /**
     * This method is used for updating an event or the whole series of events
     * @param eventId  (required)
     * @param updateEventRequest  (required)
     * @return
     */

    @Override
    public ResponseEntity<List<EventDTO>> updateEvent(String eventId, UpdateEventRequest updateEventRequest) {
        log.info("Updating event with id: {}, updateEventRequest: {}", eventId, updateEventRequest);
        List<EventDTO> eventDTOs = eventService.updateEvent(eventId, updateEventRequest);
        return ResponseEntity.ok().body(eventDTOs);
    }
}
