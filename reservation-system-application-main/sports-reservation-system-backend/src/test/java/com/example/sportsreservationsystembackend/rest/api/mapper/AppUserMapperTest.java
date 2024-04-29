package com.example.sportsreservationsystembackend.rest.api.mapper;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.Event;
import com.example.sportsreservationsystembackend.model.RecurrenceGroup;
import com.example.sportsreservationsystembackend.model.Reservation;
import com.example.sportsreservationsystembackend.model.UserRole;
import com.example.sportsreservationsystembackend.rest.mapper.AppUserMapper;
import com.xstejsk.reservationapp.main.rest.model.AppUserDTO;
import com.xstejsk.reservationapp.main.rest.model.AppUserWithReservationsDTO;
import com.xstejsk.reservationapp.main.rest.model.CreateUserRequest;
import com.xstejsk.reservationapp.main.rest.model.EventDTO;
import com.xstejsk.reservationapp.main.rest.model.Recurrence;
import com.xstejsk.reservationapp.main.rest.model.ReservationWithEventDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AppUserMapperTest {

    private LocalTime nowTime = LocalTime.parse("16:29:40.449247511");
    private LocalDate nowLocalDate = LocalDate.parse("2021-05-20");

    @Autowired
    private AppUserMapper appUserMapper;

    @Test
    void appUserToAppUserDTO() {
        AppUser appUser = buildAppUser();
        AppUserDTO expected = buildAppUserDTO();
        AppUserDTO actual = appUserMapper.appUserToAppUserDTO(appUser);
        assertEquals(expected.getId(), actual.getId());
    }

    @Test
    void appUserToAppUserWithReservationsDTO() {
        AppUser appUser = buildAppUser();
        AppUserWithReservationsDTO expected = buildAppUserWithReservationsDTO();
        AppUserWithReservationsDTO actual = appUserMapper.appUserToAppUserWithReservationsDTO(appUser);
        assertEquals(expected, actual);
    }

    @Test
    void createRequestToAppUser() {
        AppUser expected = buildAppUser();
        expected.setEnabled(false);
        expected.setLocked(false);
        expected.setId(null);
        expected.setRole(null);
        expected.setPassword("password");

        AppUser actual = appUserMapper.createRequestToAppUser(buildCreateUserRequest());
        assertEquals(expected, actual);
    }

    private CreateUserRequest buildCreateUserRequest() {
        return new CreateUserRequest()
                .email("some email")
                .firstName("some first name")
                .lastName("some last name")
                .password("password");
    }

    private AppUserWithReservationsDTO buildAppUserWithReservationsDTO() {
        AppUserWithReservationsDTO appUserDTO = new AppUserWithReservationsDTO();
        appUserDTO.setId("some uuid");
        appUserDTO.setFirstName("some first name");
        appUserDTO.setLastName("some last name");
        appUserDTO.setEmail("some email");
        appUserDTO.setEnabled(true);
        appUserDTO.setLocked(false);
        appUserDTO.setRole(AppUserWithReservationsDTO.RoleEnum.ADMIN);
        appUserDTO.setReservations(List.of(buildReservationWithEventDTO()));
        return appUserDTO;
    }

    private AppUser buildAppUser() {
        AppUser appUser = new AppUser();
        appUser.setId("some uuid");
        appUser.setFirstName("some first name");
        appUser.setLastName("some last name");
        appUser.setEmail("some email");
        appUser.setEnabled(true);
        appUser.setLocked(false);
        appUser.setRole(UserRole.ADMIN);
        appUser.setReservations(buildReservations());
        return appUser;
    }

    private Event buildEvent() {
        RecurrenceGroup recurrenceGroup = new RecurrenceGroup();
        recurrenceGroup.setDaysOfWeek(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY));
        recurrenceGroup.setRepeatUntil(nowLocalDate.plusMonths(1));

        Event event = new Event();
        event.setId("some uuid");
        event.setStartTime(nowTime);
        event.setEndTime(nowTime.plusHours(8));
        event.setMaximumCapacity(8);
        event.setPrice(100);
        event.setTitle("some title");
        event.setDescription("some description");
        event.setSpacesAvailable(8);
        event.setRecurrenceGroup(recurrenceGroup);
        return event;
    }

    private List<Reservation> buildReservations() {
        Reservation reservation = new Reservation();
        reservation.setId("some uuid");
        reservation.setEvent(buildEvent());
        return List.of(reservation);
    }

    private EventDTO buildEventDTO() {
        Recurrence recurrence = new Recurrence();
        recurrence.setRepeatUntil(nowLocalDate.plusMonths(1).toString());
        recurrence.setDaysOfWeek(List.of(1,2,3,4));

        EventDTO eventDTO = new EventDTO();
        eventDTO.setId("some uuid");
        eventDTO.setStartTime(nowTime.toString());
        eventDTO.setEndTime(nowTime.plusHours(8).toString());
        eventDTO.setMaximumCapacity(8);
        eventDTO.setPrice(100);
        eventDTO.setTitle("some title");
        eventDTO.setDescription("some description");
        eventDTO.setSpacesAvailable(8);
        eventDTO.setRecurrence(recurrence);
        return eventDTO;
    }

    private ReservationWithEventDTO buildReservationWithEventDTO() {
        ReservationWithEventDTO reservationWithEventDTO = new ReservationWithEventDTO();
        reservationWithEventDTO.setId("some uuid");
        reservationWithEventDTO.setEvent(buildEventDTO());
        return reservationWithEventDTO;
    }

    private AppUserDTO buildAppUserDTO() {
        AppUserDTO appUserDTO = new AppUserDTO();
        appUserDTO.setId("some uuid");
        appUserDTO.setFirstName("some first name");
        appUserDTO.setLastName("some last name");
        appUserDTO.setEmail("some email");
        appUserDTO.setEnabled(true);
        appUserDTO.setLocked(false);
        appUserDTO.setRole(AppUserDTO.RoleEnum.ADMIN);
        return appUserDTO;
    }
}