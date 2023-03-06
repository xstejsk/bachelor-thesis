package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.api.controller.model.UpdateEventRequest;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.FrequencyEnum;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;
import com.rstejskalprojects.reservationsystem.repository.EventRepository;
import com.rstejskalprojects.reservationsystem.util.EventDtoEventMapper;
import com.rstejskalprojects.reservationsystem.util.customexception.EventNotFoundException;
import com.rstejskalprojects.reservationsystem.util.customexception.InvalidEventTimeException;
import com.rstejskalprojects.reservationsystem.util.customexception.InvalidRecurrenceException;
import com.rstejskalprojects.reservationsystem.util.customexception.MaximumCapacityException;
import com.rstejskalprojects.reservationsystem.util.customexception.OverlappingEventException;
import com.rstejskalprojects.reservationsystem.util.customexception.RecurrenceGroupNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final ReservationService reservationService;
    private final EventRepository eventRepository;
    private final EventDtoEventMapper eventDtoToEventMapper;
    private final RecurrenceGroupService recurrenceGroupService;
    private final LocationService locationService;

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    @Transactional
    public List<Event> saveEvent(Event event) {
        if (event.getStartTime().isBefore(LocalDateTime.now())) {
            log.warn("event start time is in the past");
            throw new InvalidEventTimeException("event start time must be in the future");
        }
        if (event.getEndTime().isBefore(event.getStartTime())) {
            log.warn("event start time is after event end time");
            throw new InvalidEventTimeException("event start time must be before event end time");
        }
        if (event.getRecurrenceGroup() != null) {
            return saveRecurringEvent(event);
        }
        return List.of(saveNonRecurringEvent(event));
    }

    @Override
    public List<Event> saveEvent(EventDTO eventDTO) {
        try {
            Event event = eventDtoToEventMapper.map(eventDTO);
            return saveEvent(event);
        } catch (Exception e) {
            log.error("error while saving event", e);
            throw e;
        }
    }

    @Override
    public List<Event> findByLocationId(Long id) {
        return eventRepository.findByLocationId(id);
    }

    private Event saveNonRecurringEvent(Event event) {
        event.setRecurrenceGroup(null);
        List<Event> overlappingEvents = findOverlappingEvents(event);
        if (!overlappingEvents.isEmpty()) {
            log.warn("event overlaps with another event");
            throw new OverlappingEventException("event overlaps with events with IDs: " +
                    overlappingEvents.stream().map(overlappingEvent -> overlappingEvent.getId().toString()).collect(Collectors.joining(", ")));
        }
        log.info("saved non recurring event");
        return eventRepository.save(event);
    }

    @Override
    public List<Event> findOverlappingEvents(Event event){
        return eventRepository.findOverlappingEvents(event.getId() == null ? -1 : event.getId(), event.getLocation().getId(), event.getStartTime(), event.getEndTime());
    }


    @Override
    @Transactional
    public void deleteRecurrentEvents(Long groupId) {
        recurrenceGroupService.deleteById(groupId);
        log.info("events of recurrence group {} have been canceled", groupId);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(
                String.format("even of id %s not found", id)));
        eventRepository.deleteById(id);
        log.info("event with id {} has been deleted", id);
    }

    private List<Event> saveRecurringEvent(Event event) {
        if (event.getRecurrenceGroup().getEndDate() == null || event.getRecurrenceGroup().getEndDate().isBefore(event.getStartTime().toLocalDate())) {
            log.info("attempted to save recurrent event with end date before start date: {}", event.getRecurrenceGroup());
            throw new InvalidRecurrenceException("invalid recurrence end date");
        }
        if (event.getRecurrenceGroup().getFrequency() == FrequencyEnum.WEEKLY) {
            log.info("attempted to save weekly recurrent event with no days selected: {}", event.getRecurrenceGroup());
            return saveWeeklyRecurringEvent(event);
        }
        if (event.getRecurrenceGroup().getFrequency() == FrequencyEnum.MONTHLY) {
            log.info("attempted to save monthly recurrent event with no days selected: {}", event.getRecurrenceGroup());
            return saveMonthlyRecurringEvent(event);
        }
        log.warn("attempted to save event with invalid frequency: {}", event.getRecurrenceGroup());
        throw new IllegalArgumentException(String.format("unsupported event recurrence option %s", event.getRecurrenceGroup().getFrequency()));
    }

    private List<Event> saveMonthlyRecurringEvent(Event event) {
        LocalDateTime start = event.getStartTime();
        LocalDate recurrenceEnd = event.getRecurrenceGroup().getEndDate();
        List<LocalDate> occurrences = new ArrayList<>();
        for (LocalDate date = start.toLocalDate(); date.isBefore(recurrenceEnd); date = date.plusMonths(1)) {
            occurrences.add(date);
        }
        log.info("generated {} occurrences for monthly recurring event", occurrences.size());
        List<Event> recurringEvents = createRecurringEvents(event, occurrences);
        recurringEvents.forEach(newEvent -> {
            List<Event> overlappingEvents = findOverlappingEvents(newEvent);
            if (!overlappingEvents.isEmpty()) {
                log.warn("monthly recurrent event overlaps with another event");
                throw new OverlappingEventException("event overlaps with events with ID: " +
                        overlappingEvents.stream().map(overlappingEvent -> overlappingEvent.getId().toString()).collect(Collectors.joining(", ")));

            }
        });
        return eventRepository.saveAll(recurringEvents);
    }

    private List<Event> saveWeeklyRecurringEvent(Event event) {
        LocalDateTime start = event.getStartTime();
        LocalDate recurrenceEnd = event.getRecurrenceGroup().getEndDate();
        List<DayOfWeek> daysOfWeek = event.getRecurrenceGroup().getDaysOfWeek();

        List<LocalDate> occurrences = start.toLocalDate().datesUntil(recurrenceEnd)
                .parallel().filter(day -> daysOfWeek.contains(day.getDayOfWeek())).toList();
        log.info("saved weekly recurring event");
        List<Event> recurringEvents = createRecurringEvents(event, occurrences);
        recurringEvents.forEach(newEvent -> {
            List<Event> overlappingEvents = findOverlappingEvents(newEvent);
            if (!overlappingEvents.isEmpty()) {
                log.warn("weekly recurrent event overlaps with another event");
                throw new OverlappingEventException("event overlaps with events with IDs: " +
                        overlappingEvents.stream().map(overlappingEvent -> overlappingEvent.getId().toString()).collect(Collectors.joining(", ")));
            }
        });
        return eventRepository.saveAll(createRecurringEvents(event, occurrences));
    }

    @Override
    @Transactional
    public Event updateEvent(Long eventId, UpdateEventRequest updateEventRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(
                String.format("event of id %s not found", eventId)));
        return updateNonRecurringEvent(event, updateEventRequest);
    }

    private Event updateNonRecurringEvent(Event event, UpdateEventRequest updateEventRequest) {
        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }
        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getCapacity() != null) {
            if (reservationService.findReservationsByEventId(event.getId()).size() > updateEventRequest.getCapacity()) {
                log.warn("attempted to update event capacity to less than the number of reservations");
                throw new MaximumCapacityException("There are more existing reservations than the new capacity for the event of id " + event.getId());
            }
            event.setMaximumCapacity(updateEventRequest.getCapacity());
        }
        if (updateEventRequest.getPrice() != null) {
            event.setPrice(updateEventRequest.getPrice());
        }
        if (updateEventRequest.getLocationId() != null) {
            event.setLocation(updateEventRequest.getLocationId() == null ? event.getLocation() : locationService.findLocationById(updateEventRequest.getLocationId()));
            List<Event> overlappingEvents = findOverlappingEvents(event);
            if (!overlappingEvents.isEmpty()) {
                log.warn("attempted to update event with overlapping events");
                throw new OverlappingEventException("Location of the event cannot be changed because " +
                        "event would overlap with events with IDs: " +
                        overlappingEvents.stream().map(overlappingEvent -> overlappingEvent.getId().toString()).collect(Collectors.joining(", "))
                );
            }
        }
        log.info("updated event with id {}", event.getId());
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public List<Event> updateRecurrentEvents(Long recurrenceGroupId, UpdateEventRequest updateEventRequest) {
        List<Event> events = eventRepository.findByRecurrenceGroupId(recurrenceGroupId);
        if (events.isEmpty()) {
            log.warn("attempted to update events with invalid recurrence group id: {}", recurrenceGroupId);
            throw new RecurrenceGroupNotFoundException(String.format("recurrence group of id %s not found", recurrenceGroupId));
        }
        events.forEach(event -> updateNonRecurringEvent(event, updateEventRequest));
        return events;
    }

    private List<Event> createRecurringEvents(Event event, List<LocalDate> occurrences) {
        LocalTime startTime = event.getStartTime().toLocalTime();
        if (event.getRecurrenceGroup() == null) {
            log.warn("recurrence group is null for event: {}, could not save recurring events", event);
            throw new InvalidRecurrenceException("recurrence group must not be null");
        }
        RecurrenceGroup recurrenceGroup = recurrenceGroupService.saveRecurrenceGroup(event.getRecurrenceGroup());
        LocalTime endTime = event.getEndTime().toLocalTime();
        return occurrences.stream().map(day -> new Event(
                LocalDateTime.of(day, startTime),
                LocalDateTime.of(day, endTime),
                event.getMaximumCapacity(),
                event.getPrice(),
                event.getTitle(),
                event.getDescription(),
                recurrenceGroup,
                event.getLocation())).toList();
    }
}
