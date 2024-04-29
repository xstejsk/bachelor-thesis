package com.example.sportsreservationsystembackend.service.impl;

import com.example.sportsreservationsystembackend.exceptions.EventHasReservationsException;
import com.example.sportsreservationsystembackend.exceptions.OverlappingEventsException;
import com.example.sportsreservationsystembackend.exceptions.PastEventException;
import com.example.sportsreservationsystembackend.exceptions.ResourceNotFoundException;
import com.example.sportsreservationsystembackend.model.Calendar;
import com.example.sportsreservationsystembackend.model.Event;
import com.example.sportsreservationsystembackend.model.RecurrenceGroup;
import com.example.sportsreservationsystembackend.repository.CalendarRepository;
import com.example.sportsreservationsystembackend.repository.EventRepository;
import com.example.sportsreservationsystembackend.rest.mapper.EventsMapper;
import com.example.sportsreservationsystembackend.rest.mapper.PageMapper;
import com.example.sportsreservationsystembackend.service.EventService;
import com.example.sportsreservationsystembackend.service.RecurrenceGroupService;
import com.xstejsk.reservationapp.main.rest.model.CreateEventRequest;
import com.xstejsk.reservationapp.main.rest.model.EventDTO;
import com.xstejsk.reservationapp.main.rest.model.EventsPage;
import com.xstejsk.reservationapp.main.rest.model.UpdateEventRequest;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents event service implementation
 *
 * @Author Radim Stejskal
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CalendarRepository calendarRepository;
    private final RecurrenceGroupService recurrenceGroupService;
    private final EventsMapper eventsMapper;
    private final PageMapper pageMapper;

    /**
     * This method returns all events in given calendar
     * @param calendarId
     * @param from - start date
     * @param to - end date
     * @return
     */
    @Override
    public List<Event> getEventsByCalendarId(String calendarId, LocalDate from, LocalDate to) {
        return eventRepository.findAllByCalendarIdAndDateBetweenOrderByDateAsc(calendarId, from, to);
    }

    /**
     * This method creates either single event or recurring event based on given request
     * @param calendarId
     * @param createEventRequest
     * @return
     */
    @Override
    @Transactional
    public List<EventDTO> createEvent(String calendarId, CreateEventRequest createEventRequest) {
        Calendar calendar = calendarRepository.getReferenceById(calendarId);
        validateNewEvent(createEventRequest, calendar);
        Event event = eventsMapper.createEventRequestToEvent(createEventRequest);
        log.info("Create event: {}", event);
        if (event.getRecurrenceGroup() != null && !event.getRecurrenceGroup().getDaysOfWeek().isEmpty()) {
               return saveWeeklyRecurringEvent(event, calendar);
        }
        return saveSingleEvent(event, calendar);
    }

    /**
     * This method deletes event with given id, if it is not in the past and does not have any reservations
     * If it is the last event in recurrence group, it deletes the recurrence group as well
     * @param event
     * @param calendar
     * @return eventDTO
     */
    @Override
    @Transactional
    public EventDTO deleteEvent(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + eventId + " not found"));
        if (event.getRecurrenceGroup() != null && eventRepository.findAllByRecurrenceGroupId(event.getRecurrenceGroup().getId()).size() == 1) {
            log.info("Deleting last event from recurrence group: {}", event.getRecurrenceGroup());
            recurrenceGroupService.delete(event.getRecurrenceGroup().getId());
        }
        if (!event.getReservations().isEmpty()) {
            throw new EventHasReservationsException("Cannot delete event with reservations");
        }
        if (LocalDateTime.of(
                event.getDate(),
                event.getStartTime()
        ).isBefore(LocalDateTime.now())) {
            throw new PastEventException("Cannot delete past event");
        }
        eventRepository.delete(event);
        eventRepository.delete(event);
        return eventsMapper.eventToEventDTO(event);
    }

    /**
     * This method returns event with given id
     * @param calendarId
     * @param eventId
     * @return event
     */
    @Override
    public Event getEventById(String calendarId, String eventId) {
        return eventRepository.findByIdAndCalendarId(eventId, calendarId)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + eventId + " not found"));
    }

    /**
     * This method updates either single event or recurring event based on given request
     * @param eventId
     * @param updateEventRequest
     * @return updated events
     */
    @Override
    @Transactional
    public List<EventDTO> updateEvent(String eventId, UpdateEventRequest updateEventRequest) {
        validateUpdateEventRequest(updateEventRequest);
        if (updateEventRequest.getUpdateSeries()) {
            return updateSeries(eventId, updateEventRequest);
        }
        return updateSingleEvent(eventId, updateEventRequest);
    }

    /**
     * This method returns all events based on given parameters
     * @param from
     * @param calendarId
     * @param page
     * @param size
     * @return page of events
     */
    @Override
    public EventsPage getAll(LocalDate from, String calendarId, Integer page, Integer size) {
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size < 1) {
            size = Integer.MAX_VALUE;
        }
//        if (from == null) {
//            from = LocalDate.now();
//        }

        // Create a specification to build dynamic queries
//        LocalDate finalFrom = from;
        Specification<Event> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (from != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), from));
            }
//            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), finalFrom));
            if (calendarId != null && !calendarId.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("calendar").get("id"), calendarId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        PageRequest pageable = PageRequest.of(page, size, Sort.by("date").ascending().and(Sort.by("startTime").ascending()));
        Page<Event> events = eventRepository.findAll(specification, pageable);

        return pageMapper.toEventsPage(events);
    }

    /**
     * This method returns true if given calendar contains any future events
     * @param calendarId
     * @return
     */

    @Override
    public boolean calendarContainsFutureEvents(String calendarId) {
        return !eventRepository.findFutureEventsByCalendar(
                calendarId, LocalDate.now(), LocalTime.now()).isEmpty();
    }

    private List<EventDTO> updateSeries(String eventId, UpdateEventRequest updateEventRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + eventId + " not found"));
        if (event.getRecurrenceGroup() == null) {
            log.warn("recurrence group is null for event: {}, could not update series", event);
            throw new IllegalArgumentException("recurrence group must not be null");
        }
        eventRepository.updateEventSeries(event.getRecurrenceGroup().getId(), updateEventRequest.getTitle(), updateEventRequest.getDescription());
        return eventRepository.findAllByRecurrenceGroupId(event.getRecurrenceGroup().getId()).stream()
                .map(eventsMapper::eventToEventDTO)
                .collect(Collectors.toList());
    }

    private List<EventDTO> updateSingleEvent(String eventId, UpdateEventRequest updateEventRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + eventId + " not found"));
        event.setTitle(updateEventRequest.getTitle());
        event.setDescription(updateEventRequest.getDescription());
        eventRepository.save(event);
        return List.of(eventsMapper.eventToEventDTO(event));
    }

    private List<EventDTO> saveSingleEvent(Event event, Calendar calendar) {
        if (isEventOverlapping(event, calendar)) {
            throw new OverlappingEventsException("Event " + event + "overlaps with existing event/s");
        }
        event.setCalendar(calendar);
        event.setRecurrenceGroup(null);
        return List.of(eventsMapper.eventToEventDTO(eventRepository.save(event)));
    }

    private List<Event> createRecurringEvents(Event event, List<LocalDate> occurrences, Calendar calendar) {
        if (event.getRecurrenceGroup() == null) {
            log.warn("recurrence group is null for event: {}, could not save recurring events", event);
            throw new IllegalArgumentException("recurrence group must not be null");
        }
        RecurrenceGroup recurrenceGroup = recurrenceGroupService.save(event.getRecurrenceGroup());

        return occurrences.stream().map(day -> new Event(
                calendar,
                day,
                event.getStartTime(),
                event.getEndTime(),
                event.getMaximumCapacity(),
                event.getPrice(),
                event.getTitle(),
                event.getDescription(),
                recurrenceGroup)).toList();
    }


    private List<EventDTO> saveWeeklyRecurringEvent(Event event, Calendar calendar) {
        if (isRecurringEventOverlapping(event, calendar.getId())) {
            throw new OverlappingEventsException("Event " + event + " overlaps with existing events.");
        }
        LocalDate recurrenceStart = event.getDate();
        LocalDate recurrenceEnd = event.getRecurrenceGroup().getRepeatUntil();
        List<DayOfWeek> daysOfWeek = event.getRecurrenceGroup().getDaysOfWeek();

        List<LocalDate> occurrences = recurrenceStart.datesUntil(recurrenceEnd)
                .parallel().filter(day -> daysOfWeek.contains(day.getDayOfWeek())).toList();
        return eventRepository.saveAll(createRecurringEvents(event, occurrences, calendar)).stream().map(eventsMapper::eventToEventDTO).toList();
    }

    private void validateNewEvent(CreateEventRequest createEventRequest, Calendar calendar) {
        try {
            if (createEventRequest != null
                    && createEventRequest.getTitle() != null && !createEventRequest.getTitle().isEmpty()
                    && createEventRequest.getMaximumCapacity() != null
                    && createEventRequest.getStartTime() != null && !createEventRequest.getStartTime().isEmpty()
                    && createEventRequest.getEndTime() != null && !createEventRequest.getEndTime().isEmpty()
                    && createEventRequest.getPrice() != null) {
                LocalTime start = LocalTime.parse(createEventRequest.getStartTime());
                LocalTime end = LocalTime.parse(createEventRequest.getEndTime());
                if (start.isAfter(end)) {
                    throw new IllegalArgumentException("Start date cannot be after end date");
                }
                if (createEventRequest.getMaximumCapacity() < 1) {
                    throw new IllegalArgumentException("Capacity must be at least 1");
                }
                if (createEventRequest.getPrice() < 0) {
                    throw new IllegalArgumentException("Price cannot be negative");
                }
                if (createEventRequest.getDate() == null || LocalDate.parse(createEventRequest.getDate()).isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("Event cannot be happening in the past");
                }
                if (createEventRequest.getRecurrence() != null) {
                    if(createEventRequest.getRecurrence().getDaysOfWeek() == null) {
                        throw new IllegalArgumentException("Days of week must be specified");
                    }
                    if (createEventRequest.getRecurrence().getRepeatUntil() == null || createEventRequest.getRecurrence().getRepeatUntil().isBlank()) {
                        throw new IllegalArgumentException("Repeat until must be specified");
                    }
                    if (LocalDate.parse(createEventRequest.getRecurrence().getRepeatUntil()).isBefore(LocalDate.now())) {
                        throw new IllegalArgumentException("Recurrence end date cannot be in the past");
                    }
                }

            } else {
                throw new IllegalArgumentException("Request must contain all required fields");
            }
        } catch (Exception e) {
            log.error("error validating create event request", e);
            throw new IllegalArgumentException("Invalid request");
        }

    }

    private boolean isRecurringEventOverlapping(Event event, String calendarId) {
        List<Event> overlappingEvents = eventRepository.findOverlappingEventsForRecurringEvent(
                calendarId,
                event.getDate(),
                event.getRecurrenceGroup().getRepeatUntil(),
                event.getStartTime(),
                event.getEndTime(),
                event.getRecurrenceGroup().getDaysOfWeek().stream().map(DayOfWeek::getValue).collect(Collectors.toSet())
        );
        boolean eventOverlaps = !overlappingEvents.isEmpty();
        if (eventOverlaps){
            log.info("Event {} overlaps events {}", event, overlappingEvents);
        }
        return eventOverlaps;
    }

    private boolean isEventOverlapping(Event event, Calendar calendar) {
        List<Event> overlappingEvents = eventRepository.findOverlappingEventsForSingleEvent(
                calendar.getId(),
                event.getId(),
                event.getDate(),
                event.getStartTime(),
                event.getEndTime()
        );
        boolean eventOverlaps = !overlappingEvents.isEmpty();
        if (eventOverlaps) {
            log.info("Event {} overlaps with events {}", event, overlappingEvents);
        }
        return eventOverlaps;
    }

    private void validateUpdateEventRequest(UpdateEventRequest updateEventRequest) {
        if (updateEventRequest == null) {
            throw new IllegalArgumentException("Request must not be null");
        }
        if (updateEventRequest.getDescription() == null) {
            throw new IllegalArgumentException("Description must not be null");
        }
        if (updateEventRequest.getTitle() == null || updateEventRequest.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title must not be null or empty");
        }
        if (updateEventRequest.getUpdateSeries() == null) {
            throw new IllegalArgumentException("Update series must not be null");
        }
    }
}
