package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.model.dto.ReservationDTO;
import com.rstejskalprojects.reservationsystem.repository.EventRepository;
import com.rstejskalprojects.reservationsystem.repository.ReservationRepository;
import com.rstejskalprojects.reservationsystem.util.ReservationDtoToReservationMapper;
import com.rstejskalprojects.reservationsystem.util.customexception.AlreadyRegisteredException;
import com.rstejskalprojects.reservationsystem.util.customexception.EventNotFoundException;
import com.rstejskalprojects.reservationsystem.util.customexception.MaximumCapacityException;
import com.rstejskalprojects.reservationsystem.util.customexception.PastEventException;
import com.rstejskalprojects.reservationsystem.util.customexception.ReservationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final ReservationDtoToReservationMapper mapper;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Reservation findReservationById(Long reservationId) {
        return reservationRepository.findReservationById(reservationId).orElseThrow(() ->
                new ReservationNotFoundException(String.format("reservation of id %s not found", reservationId
        )));
    }

    @Override
    public Reservation cancelReservationById(Long reservationId, Long ownerId) throws AccessException {
        Reservation reservation = reservationRepository.findReservationById(reservationId).orElseThrow(() ->
                new ReservationNotFoundException(String.format("reservation of id %s not found", reservationId
                )));
        if (!reservation.getOwner().getId().equals(ownerId)) {
            throw new AccessException("user of id " + ownerId + " does not have access to reservation of id " + reservationId);
        }
        if (reservation.getEvent().getStartTime().isBefore(java.time.LocalDateTime.now())) {
            throw new PastEventException("event of id " + reservation.getEvent().getId() + " has already started");
        }
        reservationRepository.cancelReservationsById(reservationId);
        return reservationRepository.findReservationById(reservationId).orElseThrow(() ->
                new ReservationNotFoundException(String.format("reservation of id %s not found", reservationId
                )));
    }

    @Override
    public List<Reservation> findReservationsByUserId(Long ownerId) {
        return new ArrayList<>(reservationRepository.findByOwnerId(ownerId));
    }

    @Override
    public List<Reservation> findActivePresentReservationsByUser(Long ownerId) {
        return reservationRepository.findActivePresentReservationsByUser(ownerId);
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
    public Reservation create(Reservation reservation) {
        Long eventId = reservation.getEvent().getId();
        Long ownerId = reservation.getOwner().getId();

        if (userIsAlreadyRegisteredForEvent(ownerId, eventId)) {
            log.info("user of id {} is already registered for event of id {}", ownerId, eventId);
            throw new AlreadyRegisteredException("user of id " + ownerId + "" +
                    " is already registered for event of id " + eventId);
        }
        if (reservation.getEvent().getStartTime().isBefore(java.time.LocalDateTime.now())) {
            log.info("event of id {} has already started", eventId);
            throw new PastEventException("event of id " + eventId + " has already started");
        }
        if (eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(String.format("event of id %s not found", eventId
                ))).getCapacity() <=
                reservationRepository.findActiveReservationsByEventId(eventId).size()) {
            log.debug("event of id {} is full", eventId);
            throw new MaximumCapacityException("event of id " + eventId + " is full");
        }
        log.info("creating reservation: " + reservation);
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
    public Reservation create(ReservationDTO reservationDTO) {
        Reservation reservation = mapper.map(reservationDTO);
        return create(reservation);
    }

    @Transactional
    @Override
    public List<Reservation> cancelMultipleReservations(List<Long> reservationIds, Long ownerId) {
        List<Reservation> reservations = new ArrayList<>();
        AppUser appUser = userDetailsService.findById(ownerId);
        for (Long reservationId : reservationIds) {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new ReservationNotFoundException(String.format("reservation of id %s not found", reservationId)));
            if (appUser.getUserRole() != UserRoleEnum.ADMIN && !Objects.equals(reservation.getOwner().getId(), ownerId)) {
                throw new InvalidDataAccessResourceUsageException("the user of id " + ownerId + " is not the owner of reservation of id " + reservationId);
            }
            if (reservation.getEvent().getStartTime().isBefore(java.time.LocalDateTime.now())) {
                throw new PastEventException("event of id " + reservation.getEvent().getId() + " has already started");
            }
            reservation.setIsCanceled(true);
            reservationRepository.save(reservation);
            reservations.add(reservation);
        }
        return reservations;
    }

    private boolean userIsAlreadyRegisteredForEvent(Long userId, Long eventId) {
        Optional<Reservation> reservation = reservationRepository.findActiveReservationByEventIdAndUserId(userId, eventId);
        return reservation.isPresent();
    }
}
