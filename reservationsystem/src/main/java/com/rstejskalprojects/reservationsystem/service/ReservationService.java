package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Reservation;

import java.util.List;

public interface ReservationService {
    Reservation findReservationById(Long reservationId);

    List<Reservation> findReservationsByUserId(String username);

    List<Reservation> findReservationsByEventId(Long eventId);

    List<Reservation> findAll();
}
