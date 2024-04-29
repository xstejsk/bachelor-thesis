package com.example.sportsreservationsystembackend.rest.mapper;

import com.example.sportsreservationsystembackend.model.Reservation;
import com.xstejsk.reservationapp.main.rest.model.ReservationDTO;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * This class represents reservations mapper
 *
 * @author Radim Stejskal
 */

@Mapper(componentModel = "spring", uses = {EventsMapper.class})
@AllArgsConstructor
@Component
public abstract class ReservationsMapper {

    /**
     * This method is used for mapping reservation to reservation DTO
     * @param reservation
     * @return reservation DTO
     */
    public abstract ReservationDTO reservationToReservationDTO(Reservation reservation);


}
