package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.FrequencyEnum;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;
import com.rstejskalprojects.reservationsystem.repository.EventRepository;
import com.rstejskalprojects.reservationsystem.util.EventDtoEventMapper;
import com.rstejskalprojects.reservationsystem.util.customexception.EventNotFoundException;
import com.rstejskalprojects.reservationsystem.util.customexception.InvalidEventTimeException;
import com.rstejskalprojects.reservationsystem.util.customexception.InvalidRecurrenceException;
import com.rstejskalprojects.reservationsystem.util.customexception.OverlappingEventException;
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


    @Override
    public Event findEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(
                String.format("Event with id %s does not exist", id)));
    }

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> findAllNonCanceled() {
        return eventRepository.findAllNonCanceled();
    }

    @Override
    @Transactional
    public List<Event> saveEvent(Event event) {
        if (event.getEndTime().isBefore(event.getEndTime())) {
            log.warn("event start time is after event end time");
            throw new InvalidEventTimeException("event start time must be before event end time");
        }
        if (event.getRecurrenceGroup() != null && event.getRecurrenceGroup().getFrequency() != FrequencyEnum.NEVER) {
            return saveRecurringEvent(event);
        }
        return List.of(saveNonRecurringEvent(event));
    }

    @Override
    public List<Event> saveEvent(EventDTO eventDTO) {
        Event event = eventDtoToEventMapper.map(eventDTO);
        return saveEvent(event);
    }

    @Override
    public List<Event> findByLocationName(String name) {
        return eventRepository.findByLocationName(name);
    }

    @Override
    public List<Event> findByLocationId(Long id) {
        return eventRepository.findByLocationId(id);
    }

    private Event saveNonRecurringEvent(Event event) {
        List<RecurrenceGroup> recurrenceGroups = recurrenceGroupService.findByFrequency(FrequencyEnum.NEVER);
        RecurrenceGroup recurrenceGroup;
        if (recurrenceGroups.isEmpty()) {
            recurrenceGroup = new RecurrenceGroup(FrequencyEnum.NEVER);
        } else {
            recurrenceGroup = recurrenceGroups.get(0);
        }
        List<Event> overlappingEvents = findOverlappingEvents(event);
        if (!overlappingEvents.isEmpty()) {
            log.warn("event overlaps with another event");
            throw new InvalidEventTimeException("event overlaps with another event");
        }
        event.setRecurrenceGroup(recurrenceGroup);
        log.info("saved non recurring event");
        return eventRepository.save(event);
    }

    @Override
    public List<Event> findOverlappingEvents(Event event){
        return eventRepository.findOverlappingEvents(event.getId(), event.getRecurrenceGroup().getId(),
                event.getLocation().getId(), event.getStartTime(), event.getEndTime());
    }

    @Override
    public Event cancelEvent(EventDTO eventDTO) {
        Event event = eventRepository.findById(eventDTO.getId()).orElseThrow(() -> new EventNotFoundException(
                String.format("even of id %s not found", eventDTO.getId())));
        log.info("canceled event with id {}", event.getId());
        return cancelEvent(event);
    }

    @Override
    @Transactional
    public List<Event> cancelRecurrentEvents(Long groupId) {
        eventRepository.cancelEventByGroupId(groupId);
        reservationService.cancelReservationsByEventGroupId(groupId);
        log.info("events of recurrence group {} have been canceled", groupId);
        return eventRepository.findEventByRecurrenceGroupId(groupId);
    }

    @Override
    @Transactional
    public Event cancelEvent(Event event) {
        Event canceledEvent = eventRepository.findById(event.getId()).orElseThrow(() -> new EventNotFoundException(
                String.format("even of id %s not found", event.getId())));
        eventRepository.cancelEventById(event.getId());
        reservationService.cancelReservationsByEventId(event.getId());
        canceledEvent.setIsCanceled(true);
        log.info("event {} has been canceled", event);
        return canceledEvent;
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

    //TODO: check collisions
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
                throw new OverlappingEventException("event overlaps with another event", overlappingEvents.stream().map(
                        EventDTO::new).toList());

            }
        });
        return eventRepository.saveAll(recurringEvents);
    }

    //TODO: check collisions
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
                throw new OverlappingEventException("event overlaps with another event", overlappingEvents.stream()
                        .map(EventDTO::new).toList());
            }
        });
        return eventRepository.saveAll(createRecurringEvents(event, occurrences));
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
                event.getCapacity(),
                event.getPrice(),
                event.getTitle(),
                event.getDescription(),
                event.getIsFull(),
                recurrenceGroup,
                event.getLocation())).toList();
    }
}
