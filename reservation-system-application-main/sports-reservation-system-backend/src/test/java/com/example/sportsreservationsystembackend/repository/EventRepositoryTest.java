package com.example.sportsreservationsystembackend.repository;

import com.example.sportsreservationsystembackend.model.Calendar;
import com.example.sportsreservationsystembackend.model.Event;
import com.example.sportsreservationsystembackend.model.Location;
import com.example.sportsreservationsystembackend.model.RecurrenceGroup;
import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringRunner.class)
@DataJpaTest
class EventRepositoryTest {

    private final LocalDate date = LocalDate.now().plusDays(4);
    private final LocalDateTime dateTime = LocalDateTime.now().plusDays(4);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private RecurrenceGroupRepository recurrenceGroupRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void findOverlappingEventsForSingleEvent_endsBefore() {
        String calendarId = calendarRepository.findAll().get(0).getId();

        Event savedEvent = new Event();
        savedEvent.setStartTime(LocalTime.parse("10:00:00"));
        savedEvent.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(savedEvent);
        eventRepository.save(savedEvent);

        Event before = new Event();
        before.setStartTime(LocalTime.parse("09:00:00"));
        before.setEndTime(LocalTime.parse("10:00:00"));
        before.setDate(date);
        assertTrue(eventRepository
                .findOverlappingEventsForSingleEvent(
                        calendarId,
                        null,
                        before.getDate(),
                        before.getStartTime(),
                        before.getEndTime()).isEmpty());
    }


    @Test
    void findOverlappingEventsForSingleEvent_startsBeforeEndsDuring() {
        String calendarId = calendarRepository.findAll().get(0).getId();

        Event savedEvent = new Event();
        savedEvent.setStartTime(LocalTime.parse("10:00:00"));
        savedEvent.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(savedEvent);
        eventRepository.save(savedEvent);

        Event beforeAndDuring = new Event();
        beforeAndDuring.setStartTime(LocalTime.parse("09:00:00"));
        beforeAndDuring.setEndTime(LocalTime.parse("10:30:00"));
        beforeAndDuring.setDate(date);
        assertEquals(1, eventRepository
                .findOverlappingEventsForSingleEvent(
                        calendarId,
                        null,
                        beforeAndDuring.getDate(),
                        beforeAndDuring.getStartTime(),
                        beforeAndDuring.getEndTime()).size());
    }

    @Test
    void findOverlappingEventsForSingleEvent_startsDuringEndsDuring() {
        String calendarId = calendarRepository.findAll().get(0).getId();

        Event savedEvent = new Event();
        savedEvent.setStartTime(LocalTime.parse("10:00:00"));
        savedEvent.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(savedEvent);
        eventRepository.save(savedEvent);

        Event during = new Event();
        during.setStartTime(LocalTime.parse("10:10:00"));
        during.setEndTime(LocalTime.parse("10:50:00"));
        during.setDate(date);
        assertEquals(1, eventRepository
                .findOverlappingEventsForSingleEvent(
                        calendarId,
                        null,
                        during.getDate(),
                        during.getStartTime(),
                        during.getEndTime()).size());

    }

    @Test
    void findOverlappingEventsForSingleEvent_startsDuringEndsAfter() {
        String calendarId = calendarRepository.findAll().get(0).getId();

        Event savedEvent = new Event();
        savedEvent.setStartTime(LocalTime.parse("10:00:00"));
        savedEvent.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(savedEvent);
        eventRepository.save(savedEvent);

        Event duringAndAfter = new Event();
        duringAndAfter.setStartTime(LocalTime.parse("10:00:00"));
        duringAndAfter.setEndTime(LocalTime.parse("12:00:00"));
        duringAndAfter.setDate(date);
        assertEquals(1, eventRepository
                .findOverlappingEventsForSingleEvent(
                        calendarId,
                        null,
                        duringAndAfter.getDate(),
                        duringAndAfter.getStartTime(),
                        duringAndAfter.getEndTime()).size());

    }

    @Test
    void findOverlappingEventsForSingleEvent_startsAfter() {
        String calendarId = calendarRepository.findAll().get(0).getId();

        Event savedEvent = new Event();
        savedEvent.setStartTime(LocalTime.parse("10:00:00"));
        savedEvent.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(savedEvent);
        eventRepository.save(savedEvent);

        Event beforeAndAfter = new Event();
        beforeAndAfter.setStartTime(LocalTime.parse("11:00:00"));
        beforeAndAfter.setEndTime(LocalTime.parse("12:00:00"));
        beforeAndAfter.setDate(date);
        assertEquals(0, eventRepository
                .findOverlappingEventsForSingleEvent(
                        calendarId,
                        null,
                        beforeAndAfter.getDate(),
                        beforeAndAfter.getStartTime(),
                        beforeAndAfter.getEndTime()).size());
    }

    @Test
    void findOverlappingEventsForSingleEvent_covers() {
        String calendarId = calendarRepository.findAll().get(0).getId();

        Event savedEvent = new Event();
        savedEvent.setStartTime(LocalTime.parse("10:00:00"));
        savedEvent.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(savedEvent);
        eventRepository.save(savedEvent);

        Event covers = new Event();
        covers.setStartTime(LocalTime.parse("09:00:00"));
        covers.setEndTime(LocalTime.parse("12:00:00"));
        covers.setDate(date);
        assertEquals(1, eventRepository
                .findOverlappingEventsForSingleEvent(
                        calendarId,
                        null,
                        covers.getDate(),
                        covers.getStartTime(),
                        covers.getEndTime()).size());
    }

    @Test
    void findOverlappingEventsForSingleEvent_matches() {
        String calendarId = calendarRepository.findAll().get(0).getId();

        Event savedEvent = new Event();
        savedEvent.setStartTime(LocalTime.parse("10:00:00"));
        savedEvent.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(savedEvent);
        eventRepository.save(savedEvent);

        Event covers = new Event();
        covers.setStartTime(LocalTime.parse("10:00:00"));
        covers.setEndTime(LocalTime.parse("11:00:00"));
        covers.setDate(date);
        assertEquals(1, eventRepository
                .findOverlappingEventsForSingleEvent(
                        calendarId,
                        null,
                        covers.getDate(),
                        covers.getStartTime(),
                        covers.getEndTime()).size());
    }

    @Test
    void findOverlappingEventsForSingleEvent_inBetweenEvents() {
        String calendarId = calendarRepository.findAll().get(0).getId();

        Event savedEvent1 = new Event();
        savedEvent1.setStartTime(LocalTime.parse("10:00:00"));
        savedEvent1.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(savedEvent1);
        eventRepository.save(savedEvent1);

        Event savedEvent2 = new Event();
        savedEvent2.setStartTime(LocalTime.parse("12:00:00"));
        savedEvent2.setEndTime(LocalTime.parse("13:00:00"));
        fillNonTimeData(savedEvent2);
        eventRepository.save(savedEvent2);

        Event inBetween = new Event();
        inBetween.setStartTime(LocalTime.parse("11:00:00"));
        inBetween.setEndTime(LocalTime.parse("12:00:00"));
        inBetween.setDate(date);
        assertEquals(0, eventRepository
                .findOverlappingEventsForSingleEvent(
                        calendarId,
                        null,
                        inBetween.getDate(),
                        inBetween.getStartTime(),
                        inBetween.getEndTime()).size());
    }

    @Test
    void findOverlappingEventsForRecurringEvent_covers() {
        String calendarId = calendarRepository.findAll().get(0).getId();

        Event savedEvent = new Event();
        savedEvent.setStartTime(LocalTime.parse("10:00:00"));
        savedEvent.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(savedEvent);
        eventRepository.save(savedEvent);

        assertFalse(eventRepository.findOverlappingEventsForRecurringEvent(
                calendarId,
                date.minusDays(7),
                date.plusDays(7),
                LocalTime.parse("10:00:00"),
                LocalTime.parse("11:00:00"),
                Set.of(1, 2, 3, 4, 5, 6, 7)).isEmpty());
    }

    @Test
    void findOverlappingEventsForRecurringEvent_coversAnotherCalendar() {
        String calendarId = calendarRepository.findAll().get(1).getId();

        Event savedEvent = new Event();
        savedEvent.setStartTime(LocalTime.parse("10:00:00"));
        savedEvent.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(savedEvent);
        eventRepository.save(savedEvent);

        assertTrue(eventRepository.findOverlappingEventsForRecurringEvent(
                calendarId,
                date.minusDays(7),
                date.plusDays(7),
                LocalTime.parse("10:00:00"),
                LocalTime.parse("11:00:00"),
                Set.of(1, 2, 3, 4, 5, 6, 7)).isEmpty());
    }

    @Test
    void findOverlappingEventsForRecurringEvent_noCollisions() {
        String calendarId = calendarRepository.findAll().get(0).getId();

        Event savedEvent = new Event();
        savedEvent.setStartTime(LocalTime.parse("10:00:00"));
        savedEvent.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(savedEvent);
        eventRepository.save(savedEvent);

        assertTrue(eventRepository.findOverlappingEventsForRecurringEvent(
                calendarId,
                date.minusDays(7),
                date.plusDays(7),
                LocalTime.parse("11:00:00"),
                LocalTime.parse("12:00:00"),
                Set.of(date.getDayOfWeek().getValue())).isEmpty());
    }

    @Test
    void findOverlappingEventsForRecurringEvent_sameDay() {
        String calendarId = calendarRepository.findAll().get(0).getId();

        Event savedEvent = new Event();
        savedEvent.setStartTime(LocalTime.parse("10:00:00"));
        savedEvent.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(savedEvent);
        eventRepository.save(savedEvent);

        assertFalse(eventRepository.findOverlappingEventsForRecurringEvent(
                calendarId,
                date.minusDays(7),
                date.plusDays(7),
                LocalTime.parse("10:30:00"),
                LocalTime.parse("12:00:00"),
                Set.of(date.getDayOfWeek().getValue())).isEmpty());
    }

    @Test
    void findEventsByCalendarIdAndBetweenDates() {
        Calendar calendar = new Calendar();
        calendar.setName("Another tennis hall");
        Location location = new Location();
        location.setName("New gym");
        calendar.setLocation(locationRepository.save(location));
        calendar = calendarRepository.save(calendar);

        Event event1 = new Event();
        event1.setStartTime(LocalTime.parse("10:00:00"));
        event1.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(event1);
        event1.setCalendar(calendar);
        event1.setDate(date);
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setStartTime(LocalTime.parse("10:00:00"));
        event2.setEndTime(LocalTime.parse("11:00:00"));
        event2.setTitle("Event in between");
        fillNonTimeData(event2);
        event2.setDate(date.plusDays(7));
        event2.setCalendar(calendar);
        eventRepository.save(event2);

        Event event3 = new Event();
        event3.setStartTime(LocalTime.parse("10:00:00"));
        event3.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(event3);
        event3.setDate(date.plusDays(5));
        eventRepository.save(event3);

        Event event4 = new Event();
        event4.setStartTime(LocalTime.parse("10:00:00"));
        event4.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(event4);
        event4.setDate(date.plusDays(3));
        event4.setCalendar(calendar);

        eventRepository.save(event4);

        assertEquals(3, eventRepository.findAllByCalendarIdAndDateBetweenOrderByDateAsc(calendar.getId(), date , date.plusDays(7)).size());
    }

    @Test
    public void saveGroup() {
        RecurrenceGroup recurrenceGroup = new RecurrenceGroup();
        recurrenceGroup.setDaysOfWeek(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
        recurrenceGroup.setRepeatUntil(LocalDate.now().plusDays(4));
        recurrenceGroupRepository.save(recurrenceGroup);
        TestCase.assertEquals(1, recurrenceGroupRepository.findAll().size());

    }

    @Test
    public void calendarContainsFutureEvents() {
        Location location = new Location();
        location.setName("Gymung");
        Calendar calendar = new Calendar();
        calendar.setName("random name");
        calendar.setLocation(locationRepository.save(location));
        calendar = calendarRepository.save(calendar);
        Event event = new Event();
        event.setCalendar(calendar);
        event.setDate(LocalDate.now().plusDays(1));
        event.setStartTime(LocalTime.now());
        event.setEndTime(LocalTime.now().plusHours(1));
        event.setMaximumCapacity(4);
        event.setPrice(100);
        event.setTitle("Tennis");
        eventRepository.save(event);

        assertFalse(eventRepository.findFutureEventsByCalendar(calendar.getId(), LocalDate.now(), LocalTime.now()).isEmpty());
    }

    @BeforeEach
    void setUp() {
        addCalendars();
    }

    private List<Calendar> addCalendars() {
        Location location = new Location();
        location.setName("Gym");
        Calendar calendar1 = new Calendar();
        calendar1.setName("Tennis");
        calendar1.setLocation(locationRepository.save(location));
        Calendar calendar2 = new Calendar();
        calendar2.setName("Yoga");
        calendar2.setLocation(locationRepository.save(location));
        return calendarRepository.saveAll(List.of(calendar1, calendar2));
    }

    private void fillNonTimeData(Event event) {
        event.setDate(date);
        event.setMaximumCapacity(4);
        event.setPrice(100);
        event.setTitle("Tennis");
        event.setCalendar(calendarRepository.findAll().get(0));
    }

}