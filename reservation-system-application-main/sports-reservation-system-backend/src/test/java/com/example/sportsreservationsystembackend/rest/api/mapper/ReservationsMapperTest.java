package com.example.sportsreservationsystembackend.rest.api.mapper;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.Event;
import com.example.sportsreservationsystembackend.model.RecurrenceGroup;
import com.example.sportsreservationsystembackend.model.Reservation;
import com.example.sportsreservationsystembackend.model.UserRole;
import com.example.sportsreservationsystembackend.rest.mapper.ReservationsMapper;
import com.xstejsk.reservationapp.main.rest.model.AppUserDTO;
import com.xstejsk.reservationapp.main.rest.model.EventDTO;
import com.xstejsk.reservationapp.main.rest.model.Recurrence;
import com.xstejsk.reservationapp.main.rest.model.ReservationWithEventDTO;
import com.xstejsk.reservationapp.main.rest.model.ReservationWithOwnerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReservationsMapperTest {

    private LocalTime nowTime = LocalTime.parse("16:29:40.449247511");
    private LocalDate nowDate = LocalDate.parse("2021-05-20");

    @Autowired
    private ReservationsMapper reservationsMapper;

    @Test
    void reservationToReservationWithOwnerDTO() {
        Reservation reservation = buildReservation();
        var expected = buildReservationWithOwnerDTO();
        var actual = reservationsMapper.reservationToReservationWithOwnerDTO(reservation);
        assertEquals(expected, actual);
    }

    @Test
    void reservationToReservationWithEventDTO() {
        Reservation reservation = buildReservation();
        var expected = buildReservationWithEventDTO();
        var actual = reservationsMapper.reservationToReservationWithEventDTO(reservation);
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

    private AppUser buildUser() {
        AppUser appUser = new AppUser();
        appUser.setId("some uuid");
        appUser.setFirstName("some first name");
        appUser.setLastName("some last name");
        appUser.setEmail("some email");
        appUser.setLocked(false);
        appUser.setEnabled(true);
        appUser.setRole(UserRole.USER);
        return appUser;
    }

    private Reservation buildReservation() {
        Reservation reservation = new Reservation();
        reservation.setId("some uuid");
        reservation.setEvent(buildEvent());
        reservation.setOwner(buildUser());
        return reservation;
    }

    private AppUserDTO buildAppUserDTO() {
        AppUserDTO appUserDTO = new AppUserDTO();
        appUserDTO.setId("some uuid");
        appUserDTO.setEmail("some email");
        appUserDTO.setFirstName("some first name");
        appUserDTO.setLastName("some last name");
        appUserDTO.setLocked(false);
        appUserDTO.setEnabled(true);
        appUserDTO.setRole(AppUserDTO.RoleEnum.USER);
        return appUserDTO;
    }

    private ReservationWithOwnerDTO buildReservationWithOwnerDTO() {
        ReservationWithOwnerDTO reservationWithOwnerDTO = new ReservationWithOwnerDTO();
        reservationWithOwnerDTO.setId("some uuid");
        reservationWithOwnerDTO.setOwner(buildAppUserDTO());
        return reservationWithOwnerDTO;
    }

    private ReservationWithEventDTO buildReservationWithEventDTO() {
        ReservationWithEventDTO reservationWithEventDTO = new ReservationWithEventDTO();
        reservationWithEventDTO.setId("some uuid");
        reservationWithEventDTO.setEvent(buildEventDTO());
        return reservationWithEventDTO;
    }
}