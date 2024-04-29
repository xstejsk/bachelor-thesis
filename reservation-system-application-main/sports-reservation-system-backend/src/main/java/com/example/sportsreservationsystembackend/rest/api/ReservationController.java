package com.example.sportsreservationsystembackend.rest.api;

import com.example.sportsreservationsystembackend.service.ReservationService;
import com.xstejsk.reservationapp.main.rest.api.ReservationsApi;
import com.xstejsk.reservationapp.main.rest.model.ReservationDTO;
import com.xstejsk.reservationapp.main.rest.model.ReservationsPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * This class represents reservation controller
 *
 * @author Radim Stejskal
 */

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ReservationController implements ReservationsApi {

    private final ReservationService reservationService;

    /**
     * This method is used for retrieving all reservations
     * @param from filters reservations for events which start at or after from (optional)
     * @param calendarId id of a calendar (optional)
     * @param ownerId id of the owner (optional)
     * @param eventId id of the event (optional)
     * @param page  (optional)
     * @param size  (optional)
     * @return reservations page
     */

    @Override
    public ResponseEntity<ReservationsPage> getAllReservations(LocalDate from, String calendarId, String ownerId, String eventId, Integer page, Integer size) {
        log.info("getAllReservations: from={}, calendarId={}, ownerId={}, eventId={}, page={}, size={}", from, calendarId, ownerId, eventId, page, size);
        ReservationsPage reservationsPage = reservationService.getAllReservations(from, calendarId, ownerId, eventId, page, size);
        return ResponseEntity.ok().body(reservationsPage);
    }

    /**
     * This method is used for deleting a reservation, only the owner of the reservation or an admin can delete it and only if the event is not in the past
     * @param reservationId  (required)
     * @return deleted reservation
     */

    @Override
    public ResponseEntity<ReservationDTO> deleteReservation(String reservationId) {
        ReservationDTO reservationDTO = reservationService.delete(reservationId);
        return ResponseEntity.ok().body(reservationDTO);
    }
}
