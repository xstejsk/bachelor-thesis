package com.example.sportsreservationsystembackend.service;


import com.xstejsk.reservationapp.main.rest.model.ReservationDTO;
import com.xstejsk.reservationapp.main.rest.model.ReservationsPage;

import java.time.LocalDate;

public interface ReservationService {

    ReservationDTO create(String calendarId, String eventId);

    ReservationsPage getAllReservations(LocalDate from, String calendarId, String owner, String eventId, Integer page, Integer size);

    ReservationDTO delete(String reservationId);
}
