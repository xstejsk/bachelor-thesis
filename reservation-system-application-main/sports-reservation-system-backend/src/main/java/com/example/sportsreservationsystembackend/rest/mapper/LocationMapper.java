package com.example.sportsreservationsystembackend.rest.mapper;

import com.example.sportsreservationsystembackend.model.Location;
import com.xstejsk.reservationapp.main.rest.model.LocationDTO;
import org.mapstruct.Mapper;

/**
 * This class represents location mapper
 *
 * @author Radim Stejskal
 */
@Mapper(componentModel = "spring")
public abstract class LocationMapper {

    /**
     * This method is used for mapping location to location DTO
     * @param location
     * @return
     */
    public abstract LocationDTO toDto(Location location);
}
