package com.rstejskalprojects.reservationsystem.util;

import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;
import com.rstejskalprojects.reservationsystem.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EventDtoToEventMapper implements DtoMapper<EventDTO, Event> {

    private final LocationService locationService;
    //TODO: validation
    @Override
    public Event map(EventDTO eventDTO){
        Location location = locationService.findLocationById(eventDTO.getLocationId());
        return new Event(eventDTO.getStart(),
                eventDTO.getEnd(),
                eventDTO.getCapacity(),
                eventDTO.getPrice(),
                eventDTO.getTitle(),
                eventDTO.getDescription(),
                eventDTO.getIsFull(),
                eventDTO.getRecurrenceGroup(),
                location);


    }

}