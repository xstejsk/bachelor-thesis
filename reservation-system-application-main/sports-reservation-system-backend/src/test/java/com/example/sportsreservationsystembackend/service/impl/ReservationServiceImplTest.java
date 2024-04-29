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
import org.junit.Test;
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

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {



//    @Autowired
//    private ReservationRepository reservationRepository;
//
//    @Autowired
//    private CalendarRepository calendarRepository;
//
//    @Autowired
//    private EventRepository eventRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ReservationService reservationService;
//
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

    ReservationService reservationService = new ReservationServiceImpl(eventService, userService, reservationRepository, reservationsMapper, pageMapper, notificationService);

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
        reservationService.create(calendarId, events.get(3).getId());
        when(userService.getCurrentUser()).thenReturn(userRepository.findAll().get(1));
        assertThrows(PastEventException.class, () -> reservationService.create(calendarId, events.get(0).getId()));
        reservationService.create(calendarId, events.get(1).getId());
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
    }


//    @Test
//    void findAllByFilters() {
//        Calendar calendar = addFirstCalendar();
//        Event event1 = new Event();
//        event1.setStartTime(LocalTime.parse("10:00:00"));
//        event1.setEndTime(LocalTime.parse("11:00:00"));
//        fillNonTimeData(event1);
//        event1.setDate(date.plusDays(1));
//        eventRepository.save(event1);
//
//        Event event2 = new Event();
//        event2.setStartTime(LocalTime.parse("10:00:00"));
//        event2.setEndTime(LocalTime.parse("11:00:00"));
//        fillNonTimeData(event2);
//        event2.setDate(date.plusDays(2));
//        eventRepository.save(event2);
//
//        Event event3 = new Event();
//        event3.setStartTime(LocalTime.parse("10:00:00"));
//        event3.setEndTime(LocalTime.parse("11:00:00"));
//        fillNonTimeData(event3);
//        event3.setDate(date.plusDays(3));
//        eventRepository.save(event3);
//
//        AppUser user1 = addFirstUser();
//
//        Reservation reservation1 = new Reservation();
//        reservation1.setEvent(event1);
//        reservation1.setOwner(user1);
//
//        Reservation reservation2 = new Reservation();
//        reservation2.setEvent(event2);
//        reservation2.setOwner(user1);
//
//        Reservation reservation3 = new Reservation();
//        reservation3.setEvent(event3);
//        reservation3.setOwner(user1);
//
//
//        reservationRepository.saveAll(List.of(reservation1, reservation2, reservation3));
//
//        assertEquals(2, reservationService.getAllReservations(date.plusDays(2), calendar.getId(), user1.getId(), "", 0,2 ).getContent().size());
//        assertEquals(2, reservationService.getAllReservations(date.plusDays(2), calendar.getId(), user1.getId(), "", 0,3 ).getContent().size());
//        assertEquals(2, reservationService.getAllReservations(date.plusDays(2), calendar.getId(), user1.getId(), "", 0,4 ).getContent().size());
//        assertEquals(1, reservationService.getAllReservations(date.plusDays(2), calendar.getId(), user1.getId(), "", 0,1 ).getContent().size());
//        assertEquals(1, reservationService.getAllReservations(date, calendar.getId(), user1.getId(), "", 1,2 ).getContent().size());
//        assertEquals(reservation3.getId(), reservationService.getAllReservations(date, calendar.getId(), user1.getId(), "", 1,2 ).getContent().get(0).getId());
//        assertEquals(reservation3.getId(), reservationService.getAllReservations(null, calendar.getId(), user1.getId(), "", 1,2 ).getContent().get(0).getId());
//    }
//
//    @Test
//    void findAllByFilters_noUser() {
//        Calendar calendar = addFirstCalendar();
//        Event event1 = new Event();
//        event1.setStartTime(LocalTime.parse("10:00:00"));
//        event1.setEndTime(LocalTime.parse("11:00:00"));
//        fillNonTimeData(event1);
//        event1.setDate(date.plusDays(1));
//        eventRepository.save(event1);
//
//        Event event2 = new Event();
//        event2.setStartTime(LocalTime.parse("10:00:00"));
//        event2.setEndTime(LocalTime.parse("11:00:00"));
//        fillNonTimeData(event2);
//        event2.setDate(date.plusDays(2));
//        eventRepository.save(event2);
//
//        Event event3 = new Event();
//        event3.setStartTime(LocalTime.parse("10:00:00"));
//        event3.setEndTime(LocalTime.parse("11:00:00"));
//        fillNonTimeData(event3);
//        event3.setDate(date.plusDays(3));
//        eventRepository.save(event3);
//
//        AppUser user1 = addFirstUser();
//        AppUser user2 = addSecondUser();
//
//        Reservation reservation1 = new Reservation();
//        reservation1.setEvent(event1);
//        reservation1.setOwner(user1);
//
//        Reservation reservation2 = new Reservation();
//        reservation2.setEvent(event2);
//        reservation2.setOwner(user1);
//
//        Reservation reservation3 = new Reservation();
//        reservation3.setEvent(event3);
//        reservation3.setOwner(user1);
//
//        Reservation reservation4 = new Reservation();
//        reservation4.setEvent(event1);
//        reservation4.setOwner(user2);
//
//        Reservation reservation5 = new Reservation();
//        reservation5.setEvent(event2);
//        reservation5.setOwner(user2);
//
//        Reservation reservation6 = new Reservation();
//        reservation6.setEvent(event3);
//        reservation6.setOwner(user2);
//
//        reservationRepository.saveAll(List.of(reservation1, reservation2, reservation3, reservation4, reservation5, reservation6));
//
//        assertEquals(6, reservationService.getAllReservations(null, calendar.getId(), null, "", 0,10).getContent().size());
//    }
//
//    @Test
//    void findAllByFilters_noCalendar() {
//        Calendar firstCalendar = addFirstCalendar();
//        Calendar secondCalendar = addSecondCalendar();
//
//        Event event1 = new Event();
//        event1.setStartTime(LocalTime.parse("10:00:00"));
//        event1.setEndTime(LocalTime.parse("11:00:00"));
//        fillNonTimeData(event1);
//        event1.setCalendar(firstCalendar);
//        event1.setDate(date.plusDays(1));
//        eventRepository.save(event1);
//
//        Event event2 = new Event();
//        event2.setStartTime(LocalTime.parse("10:00:00"));
//        event2.setEndTime(LocalTime.parse("11:00:00"));
//        fillNonTimeData(event2);
//        event2.setDate(date.plusDays(2));
//        event2.setCalendar(firstCalendar);
//        eventRepository.save(event2);
//
//        Event event3 = new Event();
//        event3.setStartTime(LocalTime.parse("10:00:00"));
//        event3.setEndTime(LocalTime.parse("11:00:00"));
//        fillNonTimeData(event3);
//        event3.setDate(date.plusDays(3));
//        event3.setCalendar(secondCalendar);
//        eventRepository.save(event3);
//
//        AppUser user1 = addFirstUser();
//        AppUser user2 = addSecondUser();
//
//        Reservation reservation1 = new Reservation();
//        reservation1.setEvent(event1);
//        reservation1.setOwner(user1);
//
//        Reservation reservation2 = new Reservation();
//        reservation2.setEvent(event2);
//        reservation2.setOwner(user1);
//
//        Reservation reservation3 = new Reservation();
//        reservation3.setEvent(event3);
//        reservation3.setOwner(user1);
//
//        Reservation reservation4 = new Reservation();
//        reservation4.setEvent(event1);
//        reservation4.setOwner(user2);
//
//        Reservation reservation5 = new Reservation();
//        reservation5.setEvent(event2);
//        reservation5.setOwner(user2);
//
//        Reservation reservation6 = new Reservation();
//        reservation6.setEvent(event3);
//        reservation6.setOwner(user2);
//
//        reservationRepository.saveAll(List.of(reservation1, reservation2, reservation3, reservation4, reservation5, reservation6));
//
//        assertEquals(6, reservationService.getAllReservations(null, null, null, "", 0,6).getContent().size());
//
//        assertEquals(4, reservationService.getAllReservations(null, firstCalendar.getId(), null, "", 0,6).getContent().size());
//    }
//
//    private Calendar addFirstCalendar() {
//        Calendar calendar = new Calendar();
//        calendar.setName("Tennis hall");
//        return calendarRepository.save(calendar);
//    }
//
//    private Calendar addSecondCalendar() {
//        Calendar calendar = new Calendar();
//        calendar.setName("Second Tennis hall");
//        return calendarRepository.save(calendar);
//    }
//
//    private void fillNonTimeData(Event event) {
//        event.setDate(date);
//        event.setMaximumCapacity(4);
//        event.setPrice(100);
//        event.setTitle("Tennis");
//        event.setCalendar(calendarRepository.findAll().get(0));
//    }
//
//    private AppUser addFirstUser() {
//        AppUser appUser = new AppUser();
//        appUser.setFirstName("user1");
//        appUser.setLastName("user1");
//        appUser.setEmail("user1@gmail.com");
//        appUser.setEnabled(true);
//        appUser.setLocked(false);
//        appUser.setRole(UserRole.USER);
//        appUser.setPassword("some password");
//        return userRepository.save(appUser);
//    }
//
//    private AppUser addSecondUser() {
//        AppUser appUser = new AppUser();
//        appUser.setFirstName("user2");
//        appUser.setLastName("user2");
//        appUser.setEmail("user2@gmail.com");
//        appUser.setEnabled(true);
//        appUser.setLocked(false);
//        appUser.setRole(UserRole.USER);
//        appUser.setPassword("some password");
//        return userRepository.save(appUser);
//    }


}