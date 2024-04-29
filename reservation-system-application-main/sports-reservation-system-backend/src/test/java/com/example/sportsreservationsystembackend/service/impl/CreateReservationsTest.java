package com.example.sportsreservationsystembackend.service.impl;

import com.example.sportsreservationsystembackend.exceptions.DuplicateReservationException;
import com.example.sportsreservationsystembackend.exceptions.EventFullException;
import com.example.sportsreservationsystembackend.exceptions.InsufficientFundsException;
import com.example.sportsreservationsystembackend.exceptions.PastEventException;
import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.Calendar;
import com.example.sportsreservationsystembackend.model.Event;
import com.example.sportsreservationsystembackend.model.Location;
import com.example.sportsreservationsystembackend.model.Reservation;
import com.example.sportsreservationsystembackend.model.UserRole;
import com.example.sportsreservationsystembackend.repository.CalendarRepository;
import com.example.sportsreservationsystembackend.repository.EventRepository;
import com.example.sportsreservationsystembackend.repository.LocationRepository;
import com.example.sportsreservationsystembackend.repository.ReservationRepository;
import com.example.sportsreservationsystembackend.repository.UserRepository;
import com.example.sportsreservationsystembackend.rest.mapper.PageMapper;
import com.example.sportsreservationsystembackend.rest.mapper.ReservationsMapper;
import com.example.sportsreservationsystembackend.service.EventService;
import com.example.sportsreservationsystembackend.service.NotificationService;
import com.example.sportsreservationsystembackend.service.ReservationService;
import com.example.sportsreservationsystembackend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@ExtendWith(MockitoExtension.class)
public class CreateReservationsTest {

    private final LocalDate date = LocalDate.now();

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventService eventService;

    @Mock
    private UserService userService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationsMapper reservationsMapper;

    @Autowired
    private PageMapper pageMapper;

    @Mock
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private LocationRepository locationRepository;

    private ReservationService reservationService;

    @BeforeEach
    void setUp(){
         reservationService = new ReservationServiceImpl(eventService, userService, reservationRepository, reservationsMapper, pageMapper, notificationService);
    }


    @Test
    void createReservations() {
        addUsers();
        addEvents();
        addReservations();
        String calendarId = calendarRepository.findAll().get(0).getId();
        List<Event> events = eventRepository.findAll();
        doNothing().when(notificationService).sendNewReservationEmail(Mockito.any(), Mockito.any());
        when(userService.getCurrentUser()).thenReturn(userRepository.findAll().get(0));
        assertThrows(DuplicateReservationException.class, () -> reservationService.create(calendarId, events.get(1).getId()));
        assertThrows(InsufficientFundsException.class, () -> reservationService.create(calendarId, events.get(2).getId()));
        Assertions.assertNotNull(reservationService.create(calendarId, events.get(3).getId()));
        when(userService.getCurrentUser()).thenReturn(userRepository.findAll().get(1));
        assertThrows(PastEventException.class, () -> reservationService.create(calendarId, events.get(0).getId()));
        Assertions.assertNotNull(reservationService.create(calendarId, events.get(1).getId()));
        assertThrows(EventFullException.class, () -> reservationService.create(calendarId, events.get(3).getId()));
    }


    private void addUsers() {
        AppUser johnDoe = new AppUser();
        johnDoe.setFirstName("John");
        johnDoe.setLastName("Doe");
        johnDoe.setEmail("JohnDoe@gmail.com");
        johnDoe.setEnabled(true);
        johnDoe.setLocked(false);
        johnDoe.setRole(UserRole.USER);
        johnDoe.setPassword("some password");
        johnDoe.setHasDailyDiscount(true);
        johnDoe.setBalance(0);

        AppUser janeSmith = new AppUser();
        janeSmith.setFirstName("Jane");
        janeSmith.setLastName("Smith");
        janeSmith.setEmail("JaneSmith@gmail.com");
        janeSmith.setEnabled(true);
        janeSmith.setLocked(false);
        janeSmith.setRole(UserRole.USER);
        janeSmith.setPassword("some password");
        janeSmith.setHasDailyDiscount(false);
        janeSmith.setBalance(1000);
        userRepository.saveAll(List.of(johnDoe, janeSmith));
    }

    private void addEvents() {
        Location location = new Location();
        location.setName("Location");

        Calendar calendar = new Calendar();
        calendar.setName("Calendar");
        calendar.setLocation(locationRepository.save(location));

        calendar = calendarRepository.save(calendar);

        Event event1 = new Event();
        event1.setStartTime(LocalTime.of(9, 0, 0));
        event1.setEndTime(LocalTime.of(10, 0, 0));
        event1.setDate(date.minusDays(1));
        event1.setPrice(100);
        event1.setDiscountPrice(0);
        event1.setMaximumCapacity(1);
        event1.setCalendar(calendar);

        Event event2 = new Event();
        event2.setStartTime(LocalTime.of(9, 0, 0));
        event2.setEndTime(LocalTime.of(10, 0, 0));
        event2.setDate(date.plusDays(1));
        event2.setPrice(100);
        event2.setDiscountPrice(0);
        event2.setMaximumCapacity(2);
        event2.setCalendar(calendar);

        Event event3 = new Event();
        event3.setStartTime(LocalTime.of(10, 0, 0));
        event3.setEndTime(LocalTime.of(13, 0, 0));
        event3.setDate(date.plusDays(1));
        event3.setPrice(100);
        event3.setDiscountPrice(0);
        event3.setMaximumCapacity(1);
        event3.setCalendar(calendar);

        Event event4 = new Event();
        event4.setStartTime(LocalTime.of(10, 0, 0));
        event4.setEndTime(LocalTime.of(13, 0, 0));
        event4.setDate(date.plusDays(2));
        event4.setPrice(100);
        event4.setDiscountPrice(0);
        event4.setMaximumCapacity(1);
        event4.setCalendar(calendar);

        eventRepository.saveAll(List.of(event1, event2, event3, event4));
    }

    private void addReservations() {
        Reservation reservation = new Reservation();
        reservation.setEvent(eventRepository.findAll().get(1));
        reservation.setOwner(userRepository.findAll().get(0));
        reservationRepository.save(reservation);
    }
}
