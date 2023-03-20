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
import com.rstejskalprojects.reservationsystem.util.customexception.IllegalResourceAccessException;
import com.rstejskalprojects.reservationsystem.util.customexception.MaximumCapacityException;
import com.rstejskalprojects.reservationsystem.util.customexception.PastEventException;
import com.rstejskalprojects.reservationsystem.util.customexception.ReservationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
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
    private final EmailSender emailSender;
    private final EmailFormatterService emailFormatterService;

    @Override
    public List<Reservation> findReservationsByUserId(Long ownerId) {
        AppUser appUser = (AppUser) userDetailsService.loadUserByUsername(((UserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        if (appUser.getUserRole().equals(UserRoleEnum.ADMIN) || appUser.getUserRole().equals(UserRoleEnum.SUPER_ADMIN) || appUser.getId().equals(ownerId)) {
            return reservationRepository.findByOwnerId(ownerId);
        } else {
            throw new IllegalResourceAccessException("You are not authorized to view this user's reservations");
        }
    }

    @Override
    public List<Reservation> findPresentReservationsByUser(Long ownerId) {
        return reservationRepository.findActivePresentReservationsByUser(ownerId);
    }

    @Override
    @Transactional
    public void deleteFutureByRecurrenceGroupId(Long recurrenceGroupId) {
        reservationRepository.deleteFutureByRecurrenceGroupId(recurrenceGroupId);
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
        AppUser appUser = (AppUser) userDetailsService.loadUserByUsername(((UserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        if (!appUser.getUserRole().equals(UserRoleEnum.ADMIN) && !appUser.getUserRole().equals(UserRoleEnum.SUPER_ADMIN) && !appUser.getId().equals(ownerId)) {
            log.error("User {} is not authorized to create reservation for user {}", appUser.getId(), ownerId);
            throw new IllegalResourceAccessException("You are not authorized to create reservations for this user");
        }
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
                ))).getMaximumCapacity() <=
                reservationRepository.findReservationsByEventId(eventId).size()) {
            log.debug("event of id {} is full", eventId);
            throw new MaximumCapacityException("event of id " + eventId + " is full");
        }
        log.info("creating reservation: " + reservation);
        Reservation newReservation = reservationRepository.save(reservation);
        notifyUserOfCreation(appUser, newReservation);
        return newReservation;
    }

    @Override
    public Reservation create(ReservationDTO reservationDTO) {
        Reservation reservation = mapper.map(reservationDTO);
        return create(reservation);
    }

    @Override
    public void deleteReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findReservationById(reservationId).orElseThrow(() ->
                new ReservationNotFoundException(String.format("reservation of id %s not found", reservationId
                )));
        AppUser appUser = (AppUser) userDetailsService.loadUserByUsername(((UserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        if (reservation.getEvent().getStartTime().isBefore(java.time.LocalDateTime.now())) {
            log.warn("event of id {} has already started, cannot delete reservation of id {}", reservation.getEvent().getId(), reservation.getId());
            throw new PastEventException("event of id " + reservation.getEvent().getId() + " has already started," +
                    "cannot delete reservation of id " + reservation.getId());
        }
        if (!Objects.equals(reservation.getOwner().getId(), appUser.getId()) && appUser.getUserRole() != UserRoleEnum.ADMIN  && appUser.getUserRole() != UserRoleEnum.SUPER_ADMIN) {
            log.warn("user of id {} does not have access to reservation of id {}", appUser.getId(), reservationId);
            throw new IllegalResourceAccessException("the user of id " + appUser.getId() + " is not the owner of reservation of id " + reservationId);
        }
        reservationRepository.deleteById(reservationId);
        notifyUserOfCancellation(appUser, reservation);
        log.info("deleted reservation of id {} for user of id {}", reservationId, appUser.getId());
    }

    private void notifyUserOfCreation(AppUser appUser, Reservation reservation) {
        try {
            String subject = "Vytvoření rezervace";
            String body = emailFormatterService.formatReservationConfirmationEmail(appUser.getFirstName(), reservation.getEvent().getTitle(), reservation.getEvent().getLocation().getName(),
                    reservation.getEvent().getStartTime().format(DateTimeFormatter.ofPattern("dd. MM. HH:mm")));
            new Thread(() -> emailSender.sendEmail(appUser.getLoginEmail(), body, subject)).start();
        } catch (Exception e) {
            log.error("error while sending email about creating a reservation to user of id {}", appUser.getId());
        }
    }

    private void notifyUserOfCancellation(AppUser appUser, Reservation reservation) {
        try {
            String eventDate = reservation.getEvent().getStartTime().format(DateTimeFormatter.ofPattern("dd. MM. HH:mm"));
            System.out.println(eventDate);
            new Thread(() -> emailSender.sendEmail(appUser.getLoginEmail(),
                    emailFormatterService
                            .formatReservationCancellationEmail(
                                    appUser.getFirstName(), reservation.getEvent().getTitle(), reservation.getEvent().getLocation().getName(), eventDate), "Zrušení rezervace")).start();
        } catch (Exception e) {
            log.error("error while sending email about cancelling a reservation to user of id {}", appUser.getId());
        }

    }

    private boolean userIsAlreadyRegisteredForEvent(Long userId, Long eventId) {
        Optional<Reservation> reservation = reservationRepository.findReservationByEventIdAndUserId(userId, eventId);
        return reservation.isPresent();
    }
}
