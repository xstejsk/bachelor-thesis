package com.rstejskalprojects.reservationsystem.service.impl;

import com.rstejskalprojects.reservationsystem.api.controller.model.UpdateEventRequest;
import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.FrequencyEnum;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;
import com.rstejskalprojects.reservationsystem.repository.EventRepository;
import com.rstejskalprojects.reservationsystem.service.EmailFormatterService;
import com.rstejskalprojects.reservationsystem.service.EmailSender;
import com.rstejskalprojects.reservationsystem.service.EventService;
import com.rstejskalprojects.reservationsystem.service.RecurrenceGroupService;
import com.rstejskalprojects.reservationsystem.service.ReservationService;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final ReservationService reservationService;
    private final EventRepository eventRepository;
    private final EventDtoEventMapper eventDtoToEventMapper;
    private final RecurrenceGroupService recurrenceGroupService;
    private final EmailFormatterService emailFormatterService;
    private final EmailSender emailSender;

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
                    overlappingEvents.stream().map(overlappingEvent ->
                            overlappingEvent.getId().toString()).collect(Collectors.joining(", ")));
        }
        log.info("saved non recurring event");
        return eventRepository.save(event);
    }

    @Override
    public List<Event> findOverlappingEvents(Event event){
        return eventRepository.findOverlappingEvents(event.getId() == null ? -1 : event.getId(),
                event.getLocation().getId(), event.getStartTime(), event.getEndTime());
    }


    @Override
    @Transactional
    public void deleteRecurrentEvents(Long groupId) {
        List<Event> recurringEvents = eventRepository.findByRecurrenceGroupId(groupId);
        if (recurringEvents.isEmpty()) {
            log.warn("no events found for recurrence group with id {}", groupId);
            throw new RecurrenceGroupNotFoundException("no events found for recurrence group with id " + groupId);
        }
        if (eventRepository.findByRecurrenceGroupId(groupId).size() == eventRepository.findFutureEventsByRecurrenceGroupId(groupId).size()) {
            recurrenceGroupService.deleteById(groupId);
            log.info("deleted all events for recurrence group with id {}", groupId);
        } else {
            reservationService.deleteFutureByRecurrenceGroupId(groupId);
            eventRepository.deleteFutureEventsByRecurrenceGroupId(groupId);
            log.info("deleted future events for recurrence group with id {}", groupId);
        }
        new Thread(() -> notifyUsersAboutCancelledRecurrentEvent(recurringEvents)).start();
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(
                String.format("even of id %s not found", id)));
        eventRepository.deleteById(id);
        log.info("event with id {} has been deleted", id);
        new Thread(() -> notifyUserAboutCancelledEvent(event)).start();
    }

    private void notifyUsersAboutCancelledRecurrentEvent(List<Event> events) {
        String subject = "Zrušení opakované události";
        Set<AppUser> recipients = events.stream()
                .flatMap(event -> event.getReservations().stream())
                .map(Reservation::getOwner)
                .collect(Collectors.toSet());
        for (AppUser recipient: recipients) {
            try {
                String body = emailFormatterService.formatRecurrentEventCancellationEmail(recipient.getFirstName(),
                        events.get(0).getTitle());
                log.info("event cancellation body: {}", body);
                emailSender.sendEmail(recipient.getLoginEmail(), body, subject);
            } catch (Exception e) {
                log.error("error while sending email about creating a reservation to user of id {}", recipient.getId());
            }
        }
    }

    private void notifyUserAboutCancelledEvent(Event event) {
        String subject = "Zrušení události";
        for (Reservation reservation: event.getReservations()) {
            try {
                AppUser owner = reservation.getOwner();
                String body = emailFormatterService.formatEventCancellationEmail(owner.getFirstName(),
                    reservation.getEvent().getTitle(), reservation.getEvent().getLocation().getName(),
                    reservation.getEvent().getStartTime().format(DateTimeFormatter.ofPattern("dd. MM. HH:mm")));
                log.info("event cancellation body: {}", body);
                emailSender.sendEmail(owner.getLoginEmail(), body, subject);
            } catch (Exception e) {
                log.error("error while sending email about creating a reservation to user of id {}", reservation.getOwner().getId());
            }
        }
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
        if (event.getStartTime().isBefore(LocalDateTime.now())) {
            log.warn("attempted to update event that has already started");
            throw new InvalidEventTimeException("event has already started");
        }
        return updateNonRecurringEvent(event, updateEventRequest);
    }

    private Event updateNonRecurringEvent(Event event, UpdateEventRequest updateEventRequest) {
        log.info("updateEventRequest capacity: {}", updateEventRequest.getMaximumCapacity());
        if (updateEventRequest.getTitle() != null && !updateEventRequest.getTitle().isBlank() &&
                !updateEventRequest.getTitle().equals(event.getTitle())) {
            event.setTitle(updateEventRequest.getTitle());
            log.info("updated event title to {}", updateEventRequest.getTitle());
        }
        if (updateEventRequest.getDescription() != null && !updateEventRequest.getDescription().isBlank() &&
                !updateEventRequest.getDescription().equals(event.getDescription())) {
            event.setDescription(updateEventRequest.getDescription());
            log.info("updated event description to {}", updateEventRequest.getDescription());
        }
        if (updateEventRequest.getMaximumCapacity() != null && !Objects.equals(updateEventRequest.getMaximumCapacity(), event.getMaximumCapacity())) {
            if (event.getMaximumCapacity() > updateEventRequest.getMaximumCapacity()) {
                log.warn("attempted to update event capacity to less than current capacity");
                throw new MaximumCapacityException("event capacity cannot be less than current capacity");
            }
            log.info("updated event capacity to {}", updateEventRequest.getMaximumCapacity());
            event.setMaximumCapacity(updateEventRequest.getMaximumCapacity());
        }
        if (updateEventRequest.getPrice() != null && !Objects.equals(updateEventRequest.getPrice(), event.getPrice())) {
            event.setPrice(updateEventRequest.getPrice());
            log.info("updated event price to {}", updateEventRequest.getPrice());
        }
        log.info("updated event with id {}", event.getId());
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public List<Event> updateRecurrentEvents(Long recurrenceGroupId, UpdateEventRequest updateEventRequest) {
        List<Event> events = eventRepository.findFutureEventsByRecurrenceGroupId(recurrenceGroupId);
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
