package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.dto.ReservationDTO;

import java.util.List;

public interface ReservationService {
    Reservation findReservationById(Long reservationId);

    List<Reservation> findReservationsByUserId(String username);

    List<Reservation> findReservationsByEventId(Long eventId);

    List<Reservation> findAll();

    Reservation save(Reservation reservation);

    Reservation save(ReservationDTO reservationDTO);

    List<Reservation> cancelReservationsByEventId(Long id);

    List<Reservation> cancelReservationsByEventGroupId(Long groupId);
}
