package com.rstejskalprojects.reservationsystem.util;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.dto.ReservationDTO;
import com.rstejskalprojects.reservationsystem.repository.EventRepository;
import com.rstejskalprojects.reservationsystem.repository.UserRepository;
import com.rstejskalprojects.reservationsystem.util.customexception.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ReservationDtoToReservationMapper implements DtoMapper<ReservationDTO, Reservation> {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public Reservation map(ReservationDTO reservationDTO){
        Event event = eventRepository.findById(reservationDTO.getEventId()).orElseThrow(() -> new EventNotFoundException(String.format("event of id %s not found", reservationDTO.getEventId())));
        AppUser appUser = userRepository.findById(reservationDTO.getOwnerId()).orElseThrow(() -> new EventNotFoundException(String.format("user of id %s not found", reservationDTO.getOwnerId())));
        return new Reservation(appUser, event);
    }
}
