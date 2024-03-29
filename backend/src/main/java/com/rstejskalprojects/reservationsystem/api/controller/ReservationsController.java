package com.rstejskalprojects.reservationsystem.api.controller;
import com.rstejskalprojects.reservationsystem.api.controller.model.CancelReservationsRequest;
import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.dto.ReservationDTO;
import com.rstejskalprojects.reservationsystem.service.ReservationService;
import com.rstejskalprojects.reservationsystem.util.AuthorizationUtil;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import com.rstejskalprojects.reservationsystem.util.customexception.AlreadyRegisteredException;
import com.rstejskalprojects.reservationsystem.util.customexception.EventNotFoundException;
import com.rstejskalprojects.reservationsystem.util.customexception.IllegalResourceAccessException;
import com.rstejskalprojects.reservationsystem.util.customexception.MaximumCapacityException;
import com.rstejskalprojects.reservationsystem.util.customexception.PastEventException;
import com.rstejskalprojects.reservationsystem.util.customexception.ReservationNotFoundException;
import com.rstejskalprojects.reservationsystem.util.customexception.UserIdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/reservations", produces="application/json")
@RequiredArgsConstructor
@Slf4j
public class ReservationsController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAll(HttpServletRequest request, HttpServletResponse response) {
        log.info("getting all reservations");
        List<ReservationDTO> reservationDTOS = reservationService.findAll().stream().map(ReservationDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(reservationDTOS, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getByUsername(@PathVariable("userId") Long userId, @RequestParam(required=false) Boolean present, HttpServletRequest request) {
        try {
            List<ReservationDTO> reservationDTOS;
            if (present == null || !present) {
                reservationDTOS = reservationService.findReservationsByUserId(userId).stream().map(ReservationDTO::new).collect(Collectors.toList());
                log.info("getting all reservations for user with id: " + userId);
            } else {
                reservationDTOS = reservationService.findPresentReservationsByUser(userId).stream().map(ReservationDTO::new).collect(Collectors.toList());
                log.info("getting all present reservations for user with id: " + userId);
            }
            return new ResponseEntity<>(reservationDTOS, HttpStatus.OK);
        } catch (IllegalResourceAccessException e) {
            log.warn("user id does not match jwt");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserIdNotFoundException e) {
            log.warn("user id not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("error getting reservations by user id", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReservation(@RequestBody @Valid ReservationDTO reservationDTO, HttpServletRequest request) {
        try {
            reservationService.create(reservationDTO);
            return new ResponseEntity<>("New reservation has been created", HttpStatus.CREATED);
        } catch (IllegalResourceAccessException e) {
            log.warn("user id does not match jwt");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (EventNotFoundException e) {
            log.warn("event not found");
            return new ResponseEntity<>("event with the given ID was not found", HttpStatus.NOT_FOUND);
        } catch (MaximumCapacityException | PastEventException | AlreadyRegisteredException e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.warn("something went wrong", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{reservationId}")
    @Transactional
    public ResponseEntity<String> deleteReservation(@PathVariable("reservationId") Long reservationId, HttpServletRequest request) {
        try {
            reservationService.deleteReservationById(reservationId);
            return new ResponseEntity<>("The reservation has been deleted",HttpStatus.OK);
        } catch (ReservationNotFoundException e) {
            log.warn("reservation not found", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalResourceAccessException | UserIdNotFoundException e) {
            log.warn("user not authorized to cancel reservation", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (PastEventException e) {
            log.warn("event is in the past", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}