package com.example.sportsreservationsystembackend.service.impl;

import com.example.sportsreservationsystembackend.exceptions.DuplicateReservationException;
import com.example.sportsreservationsystembackend.exceptions.EventFullException;
import com.example.sportsreservationsystembackend.exceptions.InsufficientFundsException;
import com.example.sportsreservationsystembackend.exceptions.PastEventException;
import com.example.sportsreservationsystembackend.exceptions.ResourceNotFoundException;
import com.example.sportsreservationsystembackend.exceptions.ResourceNotOwnedException;
import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.Event;
import com.example.sportsreservationsystembackend.model.Reservation;
import com.example.sportsreservationsystembackend.model.UserRole;
import com.example.sportsreservationsystembackend.repository.ReservationRepository;
import com.example.sportsreservationsystembackend.rest.mapper.PageMapper;
import com.example.sportsreservationsystembackend.rest.mapper.ReservationsMapper;
import com.example.sportsreservationsystembackend.service.EventService;
import com.example.sportsreservationsystembackend.service.NotificationService;
import com.example.sportsreservationsystembackend.service.ReservationService;
import com.example.sportsreservationsystembackend.service.UserService;
import com.xstejsk.reservationapp.main.rest.model.ReservationDTO;
import com.xstejsk.reservationapp.main.rest.model.ReservationsPage;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents reservation service implementation
 *
 * @Author Radim Stejskal
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final EventService eventService;
    private final UserService userService;
    private final ReservationRepository reservationRepository;
    private final ReservationsMapper reservationsMapper;
    private final PageMapper pageMapper;
    private final NotificationService notificationService;

    /**
     * This method creates new reservation
     * @param calendarId calendar id of calendar where event is located
     * @param eventId event id of event to be reserved
     * @return ReservationDTO
     */
    @Override
    @Transactional
    public ReservationDTO create(String calendarId, String eventId) {
        Event event = eventService.getEventById(calendarId, eventId);
        AppUser owner = userService.getCurrentUser();
        if (reservationRepository.existsByEventIdAndAndOwnerId(eventId, owner.getId())) {
            throw new DuplicateReservationException("User already has reservation for this event");
        }
        if (event.getMaximumCapacity() <= event.getReservations().size()) {
            throw new EventFullException("Event is full");
        }
        if (event.getStartTime().isBefore(LocalTime.now()) && event.getDate().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new PastEventException("Event already started");
        }

        Reservation reservation = new Reservation();
        reservation.setOwner(owner);
        event.addReservation(reservation);
        applyDiscountIfApplicable(owner, event, reservation);
        Reservation savedReservation = reservationRepository.save(reservation);
        savedReservation.getEvent().setSpacesAvailable(savedReservation.getEvent().getSpacesAvailable() - 1);
        notificationService.sendNewReservationEmail(owner, event);
        return reservationsMapper.reservationToReservationDTO(savedReservation);
    }

    private void applyDiscountIfApplicable(AppUser appUser, Event event, Reservation reservation) {
        int price;
        log.info("Appuser has daily discount: {}", appUser.isHasDailyDiscount());
        log.info("Is first reservation of the day: {}", isFirstReservationOfTheDay(appUser, event));
        if (appUser.isHasDailyDiscount() && isFirstReservationOfTheDay(appUser, event)) {
            log.info("Applying discount for user: {}", appUser.getUsername());
            price = event.getDiscountPrice();
            reservation.setDiscountApplied(true);
        } else {
            price = event.getPrice();
            reservation.setDiscountApplied(false);
        }
        log.info("Price for reservation: {}", price);
        log.info("User balance: {}", appUser.getBalance());
        if (appUser.getBalance() < price) {
            throw new InsufficientFundsException("User has insufficient funds");
        }
        appUser.setBalance(appUser.getBalance() - price);
        userService.save(appUser);
    }

    boolean isFirstReservationOfTheDay(AppUser appUser, Event event) {
        return !reservationRepository.existsByOwnerIdAndEventDate(appUser.getId(), event.getDate());
    }

    /**
     * This returns all reservations based on parameters
     * @param from date from which reservations should be returned
     * @param calendarId calendar id of calendar where event is located
     * @param owner id of owner of reservation
     * @param eventId event id of event to be reserved
     * @param page page number
     * @param size size of page
     * @return page of reservations
     */
   @Override
   public ReservationsPage getAllReservations(LocalDate from, String calendarId, String owner, String eventId, Integer page, Integer size) {
        log.info("Get all reservations with parameters: from={}, calendarId={}, owner={}, eventId={}, page={}, size={}", from, calendarId, owner, eventId, page, size);
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size < 1) {
            size = Integer.MAX_VALUE;
        }
        AppUser currentUser = userService.getCurrentUser();

        if (owner == null || owner.isBlank()) {
            if (!currentUser.getRole().equals(UserRole.ADMIN)) {
                owner = currentUser.getId();
            }
        } else {
            if (!currentUser.getRole().equals(UserRole.ADMIN) && !currentUser.getId().equals(owner)) {
                throw new ResourceNotOwnedException("User is not owner of this reservation");
            }
        }

       String finalOwner = owner;
       Specification<Reservation> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (from != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("event").get("date"), from));
            }
            if  (calendarId != null && !calendarId.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("event").get("calendar").get("id"), calendarId));
            }
            if (finalOwner != null && !finalOwner.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("owner").get("id"), finalOwner));
            }
            if (eventId != null && !eventId.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("event").get("id"), eventId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        PageRequest pageable = PageRequest.of(page, size, Sort.by("event.date").ascending().and(Sort.by("event.startTime").ascending()));
        Page<Reservation> reservations = reservationRepository.findAll(specification, pageable);

        return pageMapper.toReservationsPage(reservations);
   }


    /**
     * This method deletes reservation
     * @param reservationId id of reservation to be deleted
     * @return deleted reservation
     */
    @Override
    public ReservationDTO delete(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found")
        );
        AppUser owner = userService.getCurrentUser();
        if (!reservation.getOwner().getId().equals(owner.getId()) && !owner.getRole().equals(UserRole.ADMIN)) {
            throw new IllegalArgumentException("User is not owner of this reservation");
        }
        if (reservation.getEvent().getDate().isBefore(LocalDate.now()) && reservation.getEvent().getStartTime().isBefore(LocalTime.now())) {
            throw new PastEventException("Event already started");
        }
        reservationRepository.delete(reservation);
        refund(reservation);
        userService.save(owner);
        log.info("Reservation with id {} deleted", reservationId);
        notificationService.sendReservationCancelledEmail(owner, reservation.getEvent());
        return reservationsMapper.reservationToReservationDTO(reservation);
    }

    private void refund(Reservation reservation) {
        AppUser owner = reservation.getOwner();
        if (reservation.isDiscountApplied()) {
            owner.setBalance(owner.getBalance() + reservation.getEvent().getDiscountPrice());
        } else {
            owner.setBalance(owner.getBalance() + reservation.getEvent().getPrice());
        }
        userService.save(owner);
    }
}
