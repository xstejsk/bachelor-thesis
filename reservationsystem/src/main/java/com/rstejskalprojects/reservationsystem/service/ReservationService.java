package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.dto.ReservationDTO;
import java.util.List;

public interface ReservationService {
    Reservation findReservationById(Long reservationId);

    List<Reservation> findReservationsByUserId(Long ownerId);

    List<Reservation> findPresentReservationsByUser(Long ownerId);

    List<Reservation> findReservationsByEventId(Long eventId);

    List<Reservation> findAll();

    Reservation create(Reservation reservation);

    Reservation create(ReservationDTO reservationDTO);

    void deleteReservationsByEventId(Long eventId);

    void deleteReservationsById(List<Long> reservationIds, Long ownerId);

    void deleteReservationsByEventGroupId(Long groupId);
}
