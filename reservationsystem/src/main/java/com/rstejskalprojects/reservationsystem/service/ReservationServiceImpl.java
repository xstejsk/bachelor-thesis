package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.dto.ReservationDTO;
import com.rstejskalprojects.reservationsystem.repository.ReservationRepository;
import com.rstejskalprojects.reservationsystem.util.ReservationDtoToReservationMapper;
import com.rstejskalprojects.reservationsystem.util.customexception.ReservationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationDtoToReservationMapper mapper;

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

    @Override
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }


    @Override
    @Transactional
    public List<Reservation> cancelReservationsByEventId(Long eventId) {
        reservationRepository.cancelReservationsByEventId(eventId);
        log.info("reservations for event of id {} have been cancelled", eventId);
        return reservationRepository.findReservationByEventId(eventId);
    }

    @Override
    @Transactional
    public List<Reservation> cancelReservationsByEventGroupId(Long groupId) {
        reservationRepository.cancelReservationsByEventGroupId(groupId);
        log.info("canceled reservations for group id {}", groupId);
        return reservationRepository.findReservationByEventGroupId(groupId);
    }

    @Override
    public Reservation save(ReservationDTO reservationDTO) {
        Reservation reservation = mapper.map(reservationDTO);
        reservationRepository.save(reservation);
        log.info("reservation saved {}", reservation);
        return reservation;
    }
}
