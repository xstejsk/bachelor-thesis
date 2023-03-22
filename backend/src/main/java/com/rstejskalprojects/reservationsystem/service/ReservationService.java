package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.dto.ReservationDTO;
import java.util.List;

public interface ReservationService {
    List<Reservation> findReservationsByUser(Long ownerId, AppUser appUser);

    List<Reservation> findPresentReservationsByUser(Long ownerId, AppUser appUser);

    List<Reservation> findReservationsByEventId(Long eventId);

    List<Reservation> findAll();

    Reservation create(Reservation reservation);

    Reservation create(ReservationDTO reservationDTO);

    Reservation findById(Long reservationId);

    void deleteReservationById(Long reservationId, AppUser appUser);

    void deleteFutureByRecurrenceGroupId(Long recurrenceGroupId);
}
