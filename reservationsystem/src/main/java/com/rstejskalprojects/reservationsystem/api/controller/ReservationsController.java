package com.rstejskalprojects.reservationsystem.api.controller;
import com.rstejskalprojects.reservationsystem.model.dto.ReservationDTO;
import com.rstejskalprojects.reservationsystem.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/reservations", produces="application/json")
@RequiredArgsConstructor
public class ReservationsController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ReservationService reservationService;

    @GetMapping("/all")
    public ResponseEntity<List<ReservationDTO>> getAll(HttpServletRequest request, HttpServletResponse response) {
        List<ReservationDTO> reservationDTOS = reservationService.findAll().stream().map(ReservationDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(reservationDTOS, HttpStatus.OK);
    }
}