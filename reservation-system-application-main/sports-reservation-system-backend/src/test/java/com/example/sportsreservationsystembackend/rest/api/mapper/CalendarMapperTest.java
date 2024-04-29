package com.example.sportsreservationsystembackend.rest.api.mapper;

import com.example.sportsreservationsystembackend.model.Calendar;
import com.example.sportsreservationsystembackend.model.Event;
import com.example.sportsreservationsystembackend.model.RecurrenceGroup;
import com.example.sportsreservationsystembackend.rest.mapper.CalendarMapper;
import com.xstejsk.reservationapp.main.rest.model.CalendarDTO;
import com.xstejsk.reservationapp.main.rest.model.CalendarWithEventsDTO;
import com.xstejsk.reservationapp.main.rest.model.CreateCalendarRequest;
import com.xstejsk.reservationapp.main.rest.model.EventDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CalendarMapperTest {

    @Autowired
    private CalendarMapper calendarMapper;

    private LocalTime nowTime = LocalTime.parse("16:29:40.449247511");
    private LocalDate nowDate = LocalDate.parse("2021-05-20");

//    @Test
//    void calendarDTOToCalendar() {
//        Calendar actual = calendarMapper.calendarDTOToCalendar(buildCalendarDTO());
//        Calendar expected = buildCalendar();
//        assertEquals(expected, actual);
//
//    }

//    @Test
//    void calendarWithEventsDTOToCalendar() {
//        Calendar actual = calendarMapper.calendarWithEventsDTOToCalendar(buildCalendarWithEventsDTO());
//        Calendar expected = buildCalendar();
//        assertEquals(expected, actual);
//    }

    @Test
    void calendarToCalendarDTO() {
        CalendarDTO actual = calendarMapper.calendarToCalendarDTO(buildCalendar());
        CalendarDTO expected = buildCalendarDTO();
        assertEquals(expected, actual);
    }

    @Test
    void calendarToCalendarWithEventsDTO() {
        Calendar calendar = buildCalendarWithEvents();
        CalendarWithEventsDTO actual = calendarMapper.calendarToCalendarWithEventsDTO(calendar);
        CalendarWithEventsDTO expected = buildCalendarWithEventsDTO();
        assertEquals(expected, actual);
    }

    @Test
    void createCalendarRequestToCalendar() {
        Calendar actual = calendarMapper.createCalendarRequestToCalendar(buildCreateCalendarRequest());

        Calendar expected = buildCalendar();
        expected.setId(null);
        assertEquals(expected, actual);
    }


    private Calendar buildCalendar() {
        Calendar calendar = new Calendar();
        calendar.setId("some uuid");
        calendar.setName("some name");

        return calendar;
    }

    private CreateCalendarRequest buildCreateCalendarRequest() {
        CreateCalendarRequest createCalendarRequest = new CreateCalendarRequest();
        createCalendarRequest.setName("some name");

        return createCalendarRequest;
    }

    private CalendarDTO buildCalendarDTO() {
        CalendarDTO calendarDTO = new CalendarDTO();
        calendarDTO.setId("some uuid");
        calendarDTO.setName("some name");

        return calendarDTO;
    }

    private CalendarWithEventsDTO buildCalendarWithEventsDTO() {
        CalendarWithEventsDTO calendarWithEventsDTO = new CalendarWithEventsDTO();
        calendarWithEventsDTO.setId("some uuid");
        calendarWithEventsDTO.setName("some name");
        calendarWithEventsDTO.setEvents(buildEventDTOs());

        return calendarWithEventsDTO;
    }

    private Calendar buildCalendarWithEvents() {
        Calendar calendar = new Calendar();
        calendar.setId("some uuid");
        calendar.setName("some name");
        calendar.setEvents(buildEvents());

        return calendar;
    }

    private List<Event> buildEvents() {
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
        return List.of(event);
    }

    private List<EventDTO> buildEventDTOs() {
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
        return List.of(eventDTO);
    }
}