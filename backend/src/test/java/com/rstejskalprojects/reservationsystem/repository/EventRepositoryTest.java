package com.rstejskalprojects.reservationsystem.repository;

import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.FrequencyEnum;
import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private RecurrenceGroupRepository recurrenceGroupRepository;

    @AfterEach
    void tearDown(){
        eventRepository.deleteAll();
        locationRepository.deleteAll();
        recurrenceGroupRepository.deleteAll();
    }

    @BeforeEach
    void init() {
        Location location = new Location("Test location", LocalTime.MIN, LocalTime.MAX);
        locationRepository.save(location);

        RecurrenceGroup recurrenceGroup = new RecurrenceGroup();
        recurrenceGroup.setFrequency(FrequencyEnum.WEEKLY);
        recurrenceGroup.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
        recurrenceGroup.setEndDate(LocalDate.now().plusMonths(1));
        recurrenceGroupRepository.save(recurrenceGroup);
    }

    @Test
    public void testFindByLocationId() {
        Location location = new Location("Test location", LocalTime.MIN, LocalTime.MAX);
        location.setId(2L);
        locationRepository.save(location);

        Event event1 = new Event(location, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), 1, 30d, "Test Event 1");
        Event event2 = new Event(location, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), 1, 30d, "Test Event 2");
        Event event3 = new Event(location, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), 1, 30d, "Test Event 3");
        eventRepository.saveAll(Arrays.asList(event1, event2, event3));

        List<Event> result = eventRepository.findByLocationId(2L);
        assertEquals(3, result.size());
    }

    @Test
    void findOverlappingEvents() {
        Location location = locationRepository.findById(1L).get();
        Event event1 = new Event(location, LocalDateTime.of(2020, 1, 1, 8, 0, 0), // 8:00 - 11:00
                LocalDateTime.of(2020, 1, 1, 11, 0, 0),1, 30d, "Test Event 1");
        Event event2 = new Event(location, LocalDateTime.of(2020, 1, 1, 13, 0, 0), // 13:00 - 15:00
                LocalDateTime.of(2020, 1, 1, 15, 0, 0),1, 30d, "Test Event 2");

        eventRepository.saveAll(Arrays.asList(event1, event2));

        // 11:00 - 13:00
        assertEquals(0, eventRepository.findOverlappingEvents(99L, location.getId(), LocalDateTime.of(2020, 1, 1, 11, 0, 0),
                LocalDateTime.of(2020, 1, 1, 13, 0, 0)).size());

        // 10:00 - 14:00
        assertEquals(2, eventRepository.findOverlappingEvents(99L, location.getId(), LocalDateTime.of(2020, 1, 1, 10, 0, 0),
                LocalDateTime.of(2020, 1, 1, 14, 0, 0)).size());

        // 7:00 - 10:00
        assertEquals(1, eventRepository.findOverlappingEvents(99L, location.getId(), LocalDateTime.of(2020, 1, 1, 7, 0, 0),
                LocalDateTime.of(2020, 1, 1, 10, 0, 0)).size());

        // 9:00 - 11:00
        assertEquals(1, eventRepository.findOverlappingEvents(99L, location.getId(), LocalDateTime.of(2020, 1, 1, 9, 0, 0),
                LocalDateTime.of(2020, 1, 1, 11, 0, 0)).size());

        // 7:00 - 12:00
        assertEquals(1, eventRepository.findOverlappingEvents(99L, location.getId(), LocalDateTime.of(2020, 1, 1, 7, 0, 0),
                LocalDateTime.of(2020, 1, 1, 12, 0, 0)).size());
    }
}