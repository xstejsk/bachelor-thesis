package com.example.sportsreservationsystembackend.rest.api.mapper;

import com.example.sportsreservationsystembackend.model.Event;
import com.example.sportsreservationsystembackend.model.RecurrenceGroup;
import com.example.sportsreservationsystembackend.rest.mapper.EventsMapper;
import com.xstejsk.reservationapp.main.rest.model.CreateEventRequest;
import com.xstejsk.reservationapp.main.rest.model.EventDTO;
import com.xstejsk.reservationapp.main.rest.model.Recurrence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class EventsMapperTest {

    private LocalDate nowDate = LocalDate.parse("2021-05-20");
    private LocalTime nowTime = LocalTime.parse("16:29:40.449247511");

    @Autowired
    private EventsMapper eventsMapper;

    @Test
    void eventDTOToEvent() {
        Event expected = buildEvent();
        EventDTO eventDTO = buildEventDTO();
        Event actual = eventsMapper.eventDTOToEvent(eventDTO);
        assertEquals(expected, actual);
    }

    @Test
    void eventToEventDTO() {
        Event event = buildEvent();
        EventDTO expected = buildEventDTO();
        EventDTO actual = eventsMapper.eventToEventDTO(event);
        assertEquals(expected, actual);
    }

    @Test
    void createEventRequestToEvent() {
        Event expected = buildEvent();
        expected.setId(null);
        expected.setSpacesAvailable(0);
        CreateEventRequest createEventRequest = buildCreateEventRequest();
        Event actual = eventsMapper.createEventRequestToEvent(createEventRequest);
        assertEquals(expected, actual);
    }

    private EventDTO buildEventDTO() {
        Recurrence recurrence = new Recurrence();
        recurrence.setRepeatUntil(nowDate.plusMonths(1).toString());
        recurrence.setDaysOfWeek(List.of(1,2,3,4));

        EventDTO eventDTO = new EventDTO();
        eventDTO.setId("some uuid");
        eventDTO.setDate(nowDate.toString());
        eventDTO.setStartTime(nowTime.toString());
        eventDTO.setEndTime(nowTime.plusHours(1).toString());
        eventDTO.setMaximumCapacity(8);
        eventDTO.setPrice(100);
        eventDTO.setTitle("some title");
        eventDTO.setDescription("some description");
        eventDTO.setSpacesAvailable(8);
        eventDTO.setRecurrence(recurrence);
        return eventDTO;
    }

    private CreateEventRequest buildCreateEventRequest() {
        Recurrence recurrence = new Recurrence();
        recurrence.setDaysOfWeek(List.of(1,2,3,4));
        recurrence.setRepeatUntil(nowDate.plusMonths(1).toString());

        CreateEventRequest createEventRequest = new CreateEventRequest();
        createEventRequest.setTitle("some title");

        createEventRequest.setDate(nowDate.toString());
        createEventRequest.setStartTime(nowTime.toString());
        createEventRequest.setEndTime(nowTime.plusHours(1).toString());
        createEventRequest.setMaximumCapacity(8);
        createEventRequest.setPrice(100);
        createEventRequest.setTitle("some title");
        createEventRequest.setDescription("some description");
        createEventRequest.setRecurrence(recurrence);
        return createEventRequest;
    }

    private Event buildEvent() {
        RecurrenceGroup recurrenceGroup = new RecurrenceGroup();
        recurrenceGroup.setDaysOfWeek(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY));
        recurrenceGroup.setRepeatUntil(nowDate.plusMonths(1));

        Event event = new Event();
        event.setId("some uuid");
        event.setDate(nowDate);
        event.setStartTime(nowTime);
        event.setEndTime(nowTime.plusHours(1));
        event.setMaximumCapacity(8);
        event.setPrice(100);
        event.setTitle("some title");
        event.setDescription("some description");
        event.setSpacesAvailable(8);
        event.setRecurrenceGroup(recurrenceGroup);
        return event;
    }
}