package com.example.sportsreservationsystembackend.repository;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.Calendar;
import com.example.sportsreservationsystembackend.model.Event;
import com.example.sportsreservationsystembackend.model.Reservation;
import com.example.sportsreservationsystembackend.model.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@DataJpaTest
class ReservationRepositoryTest {

    private final LocalDate date = LocalDate.now();
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByFilters() {
        addCalendar();
        Event event = new Event();
        event.setStartTime(LocalTime.parse("10:00:00"));
        event.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(event);
        event.setDate(date.plusDays(3));
        eventRepository.save(event);

        AppUser user1 = addFirstUser();
        AppUser user2 = addSecondUser();

        Reservation reservation1 = new Reservation();
        reservation1.setEvent(event);
        reservation1.setOwner(user1);

        Reservation reservation2 = new Reservation();
        reservation2.setEvent(event);
        reservation2.setOwner(user2);

        reservationRepository.saveAll(List.of(reservation1, reservation2));

        List<Reservation> containsSingle = reservationRepository.findAllByFilters(
                date.plusDays(2), event.getCalendar().getId(), user1.getId(), PageRequest.of(0, 2)).getContent();
        assertEquals(1, containsSingle.size());

        List<Reservation> noUserContainsBoth = reservationRepository.findAllByFilters(
                date.plusDays(2), event.getCalendar().getId(), null, PageRequest.of(0, 2)).getContent();

        assertEquals(2, noUserContainsBoth.size());

        List<Reservation> containsNone = reservationRepository.findAllByFilters(
                date.plusDays(5), event.getCalendar().getId(), user1.getId(), PageRequest.of(0, 2)).getContent();

        assertEquals(0, containsNone.size());

    }

    @Test
    void findAllByFilters_noUser() {
        addCalendar();
        Event event = new Event();
        event.setStartTime(LocalTime.parse("10:00:00"));
        event.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(event);
        event.setDate(date.plusDays(3));
        eventRepository.save(event);

        AppUser user1 = addFirstUser();
        AppUser user2 = addSecondUser();

        Reservation reservation1 = new Reservation();
        reservation1.setEvent(event);
        reservation1.setOwner(user1);

        Reservation reservation2 = new Reservation();
        reservation2.setEvent(event);
        reservation2.setOwner(user2);

        reservationRepository.saveAll(List.of(reservation1, reservation2));

        List<Reservation> reservations = reservationRepository.findAllByFilters(
                date.plusDays(2), event.getCalendar().getId(), null, PageRequest.of(0, 2)).getContent();

        assertEquals(2, reservations.size());
    }

    @Test
    void findAllByFilters_noCalendar() {
        addCalendar();
        Event event = new Event();
        event.setStartTime(LocalTime.parse("10:00:00"));
        event.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(event);
        event.setDate(date.plusDays(3));
        eventRepository.save(event);

        AppUser user1 = addFirstUser();
        AppUser user2 = addSecondUser();

        Reservation reservation1 = new Reservation();
        reservation1.setEvent(event);
        reservation1.setOwner(user1);

        Reservation reservation2 = new Reservation();
        reservation2.setEvent(event);
        reservation2.setOwner(user2);

        reservationRepository.saveAll(List.of(reservation1, reservation2));

        List<Reservation> reservations = reservationRepository.findAllByFilters(
                date.plusDays(2), null, user1.getId(), PageRequest.of(0, 2)).getContent();

        assertEquals(2, reservations.size());
    }

    @Test
    void isFirstReservationOfTheDay() {
        addCalendar();
        Event event = new Event();
        event.setStartTime(LocalTime.parse("10:00:00"));
        event.setEndTime(LocalTime.parse("11:00:00"));
        fillNonTimeData(event);
        event.setDate(date.plusDays(3));
        eventRepository.save(event);

        Event event2 = new Event();
        event2.setStartTime(LocalTime.parse("11:00:00"));
        event2.setEndTime(LocalTime.parse("12:00:00"));
        fillNonTimeData(event2);
        event2.setDate(date.plusDays(3));
        eventRepository.save(event2);

        AppUser user1 = addFirstUser();

        Reservation reservation1 = new Reservation();
        reservation1.setEvent(event);
        reservation1.setOwner(user1);

        Reservation reservation2 = new Reservation();
        reservation2.setEvent(event2);
        reservation2.setOwner(user1);

        reservationRepository.saveAll(List.of(reservation1, reservation2));

        assertTrue(reservationRepository.existsByOwnerIdAndEventDate(user1.getId(), date.plusDays(3)));
//        assertFalse(reservationRepository.isFirstReservationOfTheDay(event.getId(), user2.getId()));
    }

    private Calendar addCalendar() {
        Calendar calendar = new Calendar();
        calendar.setName("Tennis hall");
        return calendarRepository.save(calendar);
    }

    private void fillNonTimeData(Event event) {
        event.setDate(date);
        event.setMaximumCapacity(4);
        event.setPrice(100);
        event.setTitle("Tennis");
        event.setCalendar(calendarRepository.findAll().get(0));
    }

    private AppUser addFirstUser() {
        AppUser appUser = new AppUser();
        appUser.setFirstName("user1");
        appUser.setLastName("user1");
        appUser.setEmail("user1@gmail.com");
        appUser.setEnabled(true);
        appUser.setLocked(false);
        appUser.setRole(UserRole.USER);
        appUser.setPassword("some password");
        return userRepository.save(appUser);
    }

    private AppUser addSecondUser() {
        AppUser appUser = new AppUser();
        appUser.setFirstName("user2");
        appUser.setLastName("user2");
        appUser.setEmail("user2@gmail.com");
        appUser.setEnabled(true);
        appUser.setLocked(false);
        appUser.setRole(UserRole.USER);
        appUser.setPassword("some password");
        return userRepository.save(appUser);
    }
}