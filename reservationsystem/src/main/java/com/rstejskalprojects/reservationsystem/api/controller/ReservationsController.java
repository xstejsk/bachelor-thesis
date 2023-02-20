package com.rstejskalprojects.reservationsystem.api.controller;
import com.rstejskalprojects.reservationsystem.api.controller.model.CancelReservationsRequest;
import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.dto.ReservationDTO;
import com.rstejskalprojects.reservationsystem.service.ReservationService;
import com.rstejskalprojects.reservationsystem.util.AuthorizationUtil;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import com.rstejskalprojects.reservationsystem.util.customexception.AlreadyRegisteredException;
import com.rstejskalprojects.reservationsystem.util.customexception.EventNotFoundException;
import com.rstejskalprojects.reservationsystem.util.customexception.MaximumCapacityException;
import com.rstejskalprojects.reservationsystem.util.customexception.PastEventException;
import com.rstejskalprojects.reservationsystem.util.customexception.ReservationNotFoundException;
import com.rstejskalprojects.reservationsystem.util.customexception.UserIdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/reservations", produces="application/json")
@RequiredArgsConstructor
@Slf4j
public class ReservationsController {

    private final ReservationService reservationService;
    private final AuthorizationUtil authorizationUtil;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAll(HttpServletRequest request, HttpServletResponse response) {
        log.info("getting all reservations");
        List<ReservationDTO> reservationDTOS = reservationService.findAll().stream().map(ReservationDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(reservationDTOS, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ReservationDTO>> getByUsername(@PathVariable("userId") Long userId, HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (authorizationUtil.userIdMatchesJWT(userId, bearerToken)) {
            List<ReservationDTO> reservationDTOS = reservationService.findReservationsByUserId(userId).stream()
                    .map(ReservationDTO::new).collect(Collectors.toList());
            return new ResponseEntity<>(reservationDTOS, HttpStatus.OK);
        } else {
            log.warn("user id does not match jwt");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{userId}/active")
    public ResponseEntity<List<ReservationDTO>> getActiveByUsername(@PathVariable("userId") Long userId, HttpServletRequest request) {
        log.info("active reservations for user: " + userId);
        String bearerToken = request.getHeader("Authorization");
        if (authorizationUtil.userIdMatchesJWT(userId, bearerToken)) {
            List<ReservationDTO> reservationDTOS = reservationService.findActivePresentReservationsByUser(userId).stream()
                    .map(ReservationDTO::new).collect(Collectors.toList());
            return new ResponseEntity<>(reservationDTOS, HttpStatus.OK);
        } else {
            log.warn("user id does not match jwt");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody ReservationDTO reservationDTO, HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        try {
            Long userId = jwtUtil.getUserIdFromToken(bearerToken.substring(7));
            if (authorizationUtil.userIdMatchesJWT(userId, bearerToken)) {
                Reservation reservation = reservationService.create(reservationDTO);
                return new ResponseEntity<>(new ReservationDTO(reservation), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (EventNotFoundException e) {
            log.warn("event not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (MaximumCapacityException | PastEventException e) {
            log.warn("event is full or in the past", e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (AlreadyRegisteredException e) {
            log.warn("user already registered for event", e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.warn("something went wrong", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/cancel")
    public ResponseEntity<String> cancelMultipleReservations(@RequestBody CancelReservationsRequest cancelReservationsRequest, HttpServletRequest request) {
        try {
            String bearerToken = request.getHeader("Authorization");
            Long id = jwtUtil.getUserIdFromToken(bearerToken.substring(7));
            reservationService.cancelMultipleReservations(cancelReservationsRequest.getReservationIds(), id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ReservationNotFoundException e) {
            log.warn("reservation not found", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidDataAccessResourceUsageException | UserIdNotFoundException e) {
            log.warn("user not authorized to cancel reservation", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (PastEventException e) {
            log.warn("event is in the past", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteReservation(@RequestBody CancelReservationsRequest cancelReservationsRequest, HttpServletRequest request) {
        try {
            String bearerToken = request.getHeader("Authorization");
            Long id = jwtUtil.getUserIdFromToken(bearerToken.substring(7));
            reservationService.deleteReservations(cancelReservationsRequest.getReservationIds(), id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ReservationNotFoundException e) {
            log.warn("reservation not found", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidDataAccessResourceUsageException | UserIdNotFoundException e) {
            log.warn("user not authorized to cancel reservation", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (PastEventException e) {
            log.warn("event is in the past", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}