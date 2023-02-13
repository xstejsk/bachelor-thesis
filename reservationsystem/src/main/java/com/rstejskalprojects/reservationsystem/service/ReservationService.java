package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.dto.ReservationDTO;
import org.springframework.expression.AccessException;

import java.util.List;

public interface ReservationService {
    Reservation findReservationById(Long reservationId);

    List<Reservation> findReservationsByUserId(Long ownerId);

    List<Reservation> findActivePresentReservationsByUser(Long ownerId);

    List<Reservation> findReservationsByEventId(Long eventId);

    List<Reservation> findAll();

    Reservation create(Reservation reservation);

    Reservation create(ReservationDTO reservationDTO);

    List<Reservation> cancelReservationsByEventId(Long id);

    List<Reservation> cancelReservationsByEventGroupId(Long groupId);

    Reservation cancelReservationById(Long reservationId, Long ownerId) throws AccessException;

    List<Reservation> cancelMultipleReservations(List<Long> reservationIds, Long ownerId);
}
