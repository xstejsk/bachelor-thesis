package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.repository.ReservationRepository;
import com.rstejskalprojects.reservationsystem.util.customexception.ReservationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation findReservationById(Long reservationId) {
        return reservationRepository.findReservationById(reservationId).orElseThrow(() ->
                new ReservationNotFoundException(String.format("reservation of id %s not found", reservationId
        )));
    }

    @Override
    public List<Reservation> findReservationsByUserId(String username) {
        return new ArrayList<>(reservationRepository.findReservationByOwnerUsername(username));
    }

    @Override
    public List<Reservation> findReservationsByEventId(Long eventId) {
        return new ArrayList<>(reservationRepository.findReservationByEventId(eventId));
    }

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }
}
