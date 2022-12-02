package com.rstejskalprojects.reservationsystem.util;

import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;
import com.rstejskalprojects.reservationsystem.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EventDtoToEventMapper {

    private final LocationService locationService;
    //TODO: validation
    public Event mapToEvent(EventDTO eventDTO){
        Location location = locationService.findLocationByName(eventDTO.getLocationName());
        return new Event(eventDTO.getStartTime(),
                eventDTO.getEndTime(),
                eventDTO.getCapacity(),
                eventDTO.getPrice(),
                eventDTO.getTitle(),
                eventDTO.getDescription(),
                eventDTO.getIsAllDay(),
                eventDTO.getIsFull(),
                null, location);
    }
}
