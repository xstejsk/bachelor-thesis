package com.rstejskalprojects.reservationsystem.repository;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.FrequencyEnum;
import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.model.RecurrenceGroup;
import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RecurrenceGroupRepository recurrenceGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Test
    public void testDeleteFutureByRecurrenceGroupId() {
        // Create a recurrence group with ID 5
        RecurrenceGroup recurrenceGroup = new RecurrenceGroup();
        recurrenceGroup.setFrequency(FrequencyEnum.WEEKLY);
        recurrenceGroup.setDaysOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY));
        recurrenceGroup.setEndDate(LocalDate.now().plusDays(30));
        recurrenceGroup = recurrenceGroupRepository.save(recurrenceGroup);
        Location location = new Location("Test location", LocalTime.of(8, 0), LocalTime.of(18, 0));
        locationRepository.save(location);

        // Create an event associated with the recurrence group and with a start time in the future
        Event event = new Event();
        event.setStartTime(LocalDateTime.now().plusDays(1));
        event.setEndTime(LocalDateTime.now().plusDays(2));
        event.setMaximumCapacity(10);
        event.setPrice(20.0);
        event.setTitle("Test Event");
        event.setDescription("This is a test event");
        event.setRecurrenceGroup(recurrenceGroup);
        event.setLocation(location);
        event = eventRepository.save(event);

        // Create a reservation associated with the event
        AppUser owner = new AppUser();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setEmail("john.doe@test.com");
        owner.setPassword("password");
        owner.setUserRole(UserRoleEnum.USER);
        owner.setEnabled(true);
        owner = userRepository.save(owner);

        Reservation reservation = new Reservation();
        reservation.setOwner(owner);
        reservation.setEvent(event);
        reservationRepository.save(reservation);
        assertEquals(1, reservationRepository.count());

        reservationRepository.deleteFutureByRecurrenceGroupId(1L);
        assertEquals(0, reservationRepository.count());
    }
}