package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.dto.ReservationDTO;
import java.util.List;

public interface ReservationService {
    List<Reservation> findReservationsByUserId(Long ownerId, String jwtUserName);

    List<Reservation> findPresentReservationsByUser(Long ownerId, String jwtUserName);

    List<Reservation> findReservationsByEventId(Long eventId);

    List<Reservation> findAll();

    Reservation create(Reservation reservation);

    Reservation create(ReservationDTO reservationDTO);

    void deleteReservationById(Long reservationId);

    void deleteFutureByRecurrenceGroupId(Long recurrenceGroupId);
}
