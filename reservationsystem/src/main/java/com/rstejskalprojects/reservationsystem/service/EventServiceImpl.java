package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.FrequencyEnum;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;
import com.rstejskalprojects.reservationsystem.repository.EventRepository;
import com.rstejskalprojects.reservationsystem.util.EventDtoToEventMapper;
import com.rstejskalprojects.reservationsystem.util.customexception.EventNotFoundException;
import com.rstejskalprojects.reservationsystem.util.customexception.InvalidEventException;
import com.rstejskalprojects.reservationsystem.util.customexception.InvalidRecurrenceException;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventDtoToEventMapper eventDtoToEventMapper;
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
    @Transactional
    public List<Event> saveEvent(Event event) {
        if (event.getEndTime().isBefore(event.getEndTime())) {
            throw new InvalidEventException("event start time must be before event end time");
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
        event.setRecurrenceGroup(recurrenceGroup);
        return eventRepository.save(event);
    }


    private List<Event> saveRecurringEvent(Event event) {
        if (event.getRecurrenceGroup().getEndDate() == null || event.getRecurrenceGroup().getEndDate().isBefore(event.getStartTime().toLocalDate())) {
            log.warn("attempted to save recurrent event with end date before start date: {}", event.getRecurrenceGroup());
            throw new InvalidRecurrenceException("invalid recurrence end date");
        }
        if (event.getRecurrenceGroup().getFrequency() == FrequencyEnum.WEEKLY) {
            return saveWeeklyRecurringEvent(event);
        }
        if (event.getRecurrenceGroup().getFrequency() == FrequencyEnum.MONTHLY) {
            return saveMonthlyRecurringEvent(event);
        }
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
        return eventRepository.saveAll(createRecurringEvents(event, occurrences));
    }

    //TODO: check collisions
    private List<Event> saveWeeklyRecurringEvent(Event event) {
        if (event.getRecurrenceGroup() == null && event.getRecurrenceGroup().getFrequency() != FrequencyEnum.WEEKLY) {
            log.warn("attempted to save weekly recurrent event with invalid recurrence group: {}", event.getRecurrenceGroup());
            throw new InvalidRecurrenceException("the event recurrence must be on weekly basis");
        }
        if (event.getRecurrenceGroup().getEndDate() == null || event.getRecurrenceGroup().getEndDate().isBefore(event.getStartTime().toLocalDate())) {
            log.warn("attempted to save weekly recurrent event with end date before start date: {}", event.getRecurrenceGroup());
            throw new InvalidRecurrenceException("invalid recurrence end date");
        }
        if (event.getRecurrenceGroup().getDaysOfWeek().isEmpty()) {
            log.warn("attempted to save weekly recurrent event with empty week days: {}", event.getRecurrenceGroup());
            throw new InvalidRecurrenceException("days of week must be provided for weekly recurrence");
        }

        LocalDateTime start = event.getStartTime();
        LocalDate recurrenceEnd = event.getRecurrenceGroup().getEndDate();
        List<DayOfWeek> daysOfWeek = event.getRecurrenceGroup().getDaysOfWeek();

        List<LocalDate> occurrences = start.toLocalDate().datesUntil(recurrenceEnd)
                .parallel().filter(day -> daysOfWeek.contains(day.getDayOfWeek())).toList();
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
