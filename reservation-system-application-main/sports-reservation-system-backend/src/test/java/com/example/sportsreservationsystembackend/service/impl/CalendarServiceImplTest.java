package com.example.sportsreservationsystembackend.service.impl;

import com.example.sportsreservationsystembackend.model.Calendar;
import com.example.sportsreservationsystembackend.repository.CalendarRepository;
import com.example.sportsreservationsystembackend.rest.mapper.CalendarMapper;
import com.example.sportsreservationsystembackend.rest.mapper.PageMapper;
import com.example.sportsreservationsystembackend.service.EventService;
import com.example.sportsreservationsystembackend.service.LocationService;
import com.xstejsk.reservationapp.main.rest.model.CalendarDTO;
import com.xstejsk.reservationapp.main.rest.model.CreateCalendarRequest;
import com.xstejsk.reservationapp.main.rest.model.UpdateCalendarDetailsRequest;
import com.xstejsk.reservationapp.main.rest.model.UpdateCalendarRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private CalendarMapper calendarMapper;

    @Mock
    private EventService eventService;

    private CalendarServiceImpl calendarService;

    private final LocalTime now = LocalTime.parse("10:00:00");

    @Mock
    private LocationService locationService;

    @Mock
    private PageMapper pageMapper;

    @BeforeEach
    void setUp() {
        calendarService = new CalendarServiceImpl(locationService, calendarRepository, eventService, calendarMapper, pageMapper);
    }

    @Test
    void createCalendar() {
        when(calendarRepository.existsByName(Mockito.any())).thenReturn(false);
        when(calendarMapper.calendarToCalendarDTO(Mockito.any())).thenReturn(new CalendarDTO());
        Calendar savedCalendar = new Calendar();
        savedCalendar.setName("test-calendar");
        savedCalendar.setThumbnail("test-thumbnail".getBytes());

        when(calendarMapper.createCalendarRequestToCalendar(Mockito.any())).thenReturn(savedCalendar);
        LocalTime now = LocalTime.parse("10:00:00");

        CreateCalendarRequest createCalendarRequest = new CreateCalendarRequest();
        createCalendarRequest.setName("test-calendar");
        createCalendarRequest.setThumbnail("test-thumbnail".getBytes());

        ArgumentCaptor<Calendar> calendarArgumentCaptor = ArgumentCaptor.forClass(Calendar.class);
        calendarService.create(createCalendarRequest);
        Mockito.verify(calendarRepository).save(calendarArgumentCaptor.capture());
        Calendar calendar = calendarArgumentCaptor.getValue();



        assertEquals(savedCalendar, calendar);
    }

    @Test
    void createCalendarWithExistingName() {
        when(calendarRepository.existsByName(Mockito.any())).thenReturn(true);
//        when(calendarMapper.calendarToCalendarDTO(Mockito.any())).thenReturn(new CalendarDTO());
        LocalTime now = LocalTime.parse("10:00:00");

        CreateCalendarRequest createCalendarRequest = new CreateCalendarRequest();
        createCalendarRequest.setName("test-calendar");
        createCalendarRequest.setThumbnail("test-thumbnail".getBytes());

        assertThrows(RuntimeException.class, () -> calendarService.create(createCalendarRequest));
        Mockito.verify(calendarRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createCalendarWithInvalidRequest() {
        CreateCalendarRequest createCalendarRequest = new CreateCalendarRequest();
        // missing name
        createCalendarRequest.setThumbnail("test-thumbnail".getBytes());

        assertThrows(RuntimeException.class, () -> calendarService.create(createCalendarRequest));
        Mockito.verify(calendarRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateCalendarDetails() {
        Calendar oldCalendar = new Calendar();
        oldCalendar.setName("old-calendar");
        oldCalendar.setThumbnail("old-thumbnail".getBytes());
        oldCalendar.setId("uuid");

        when(calendarRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(oldCalendar));

        UpdateCalendarRequest updateCalendarDetailsRequest = new UpdateCalendarRequest();
        updateCalendarDetailsRequest.setName("new-calendar");
        updateCalendarDetailsRequest.setThumbnail("new-thumbnail".getBytes());

        ArgumentCaptor<Calendar> calendarArgumentCaptor = ArgumentCaptor.forClass(Calendar.class);
        calendarService.update("uuid", updateCalendarDetailsRequest);
        Mockito.verify(calendarRepository).save(calendarArgumentCaptor.capture());
        Calendar actual = calendarArgumentCaptor.getValue();

        Calendar expected = new Calendar();
        expected.setId("uuid");
        expected.setName("new-calendar");
        expected.setThumbnail("new-thumbnail".getBytes());

        assertEquals(actual, expected);
    }

    @Test
    void updateWithAllEmptyFields() {
        Calendar oldCalendar = new Calendar();
        oldCalendar.setName("old-calendar");
        oldCalendar.setThumbnail("old-thumbnail".getBytes());
        oldCalendar.setId("uuid");

        when(calendarRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(oldCalendar));

        UpdateCalendarRequest updateCalendarDetailsRequest = new UpdateCalendarRequest();
        assertThrows(RuntimeException.class, () -> calendarService.update("uuid", updateCalendarDetailsRequest));
        Mockito.verify(calendarRepository, Mockito.never()).save(Mockito.any());
    }
}