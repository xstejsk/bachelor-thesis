package com.rstejskalprojects.reservationsystem.api.controller;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.dto.ReservationDTO;
import com.rstejskalprojects.reservationsystem.service.ReservationService;
import com.rstejskalprojects.reservationsystem.util.customexception.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @GetMapping("/all")
    public ResponseEntity<List<ReservationDTO>> getAll(HttpServletRequest request, HttpServletResponse response) {
        List<ReservationDTO> reservationDTOS = reservationService.findAll().stream().map(ReservationDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(reservationDTOS, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<ReservationDTO> saveReservation(@RequestBody ReservationDTO reservationDTO) {
        Reservation reservation = reservationService.save(reservationDTO);
        return new ResponseEntity<>(new ReservationDTO(reservation), HttpStatus.CREATED);
    }

    @PutMapping("/cancel/{eventId}")
    public ResponseEntity<String> cancelReservations(@PathVariable("eventId") Long id) {
        try {
            List<Reservation> reservations = reservationService.cancelReservationsByEventId(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EventNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }
}