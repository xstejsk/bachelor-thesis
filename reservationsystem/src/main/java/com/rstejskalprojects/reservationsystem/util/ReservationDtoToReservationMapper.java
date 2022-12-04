package com.rstejskalprojects.reservationsystem.util;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.dto.ReservationDTO;
import com.rstejskalprojects.reservationsystem.service.EventService;
import com.rstejskalprojects.reservationsystem.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ReservationDtoToReservationMapper implements DtoMapper<ReservationDTO, Reservation> {

    private final EventService eventService;
    private final UserDetailsServiceImpl userDetailsService;
    //TODO: validation
    @Override
    public Reservation map(ReservationDTO reservationDTO){
        Event event = eventService.findEventById(reservationDTO.getEventId());
        AppUser appUser = userDetailsService.findById(reservationDTO.getOwner().getUserId());
        return new Reservation(appUser, event, reservationDTO.getCanceled());
    }
}
