package com.example.sportsreservationsystembackend.rest.mapper;

import com.example.sportsreservationsystembackend.model.Calendar;
import com.example.sportsreservationsystembackend.service.LocationService;
import com.example.sportsreservationsystembackend.utils.MappingService;
import com.xstejsk.reservationapp.main.rest.model.CalendarDTO;
import com.xstejsk.reservationapp.main.rest.model.CalendarWithEventsDTO;
import com.xstejsk.reservationapp.main.rest.model.CreateCalendarRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class represents calendar mapper
 *
 * @author Radim Stejskal
 */

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
//@Component
//@RequiredArgsConstructor
public abstract class CalendarMapper {

    @Autowired
    protected LocationService locationService;

    @Autowired
    protected MappingService mappingService;

    /**
     * This method is used for mapping create calendar request to calendar
     * @param calendarRequest
     * @return calendar
     */
    @Mapping(target= "location", expression = "java(locationService.findById(calendarRequest.getLocationId()))")
    public abstract Calendar createCalendarRequestToCalendar(CreateCalendarRequest calendarRequest);

    /**
     * This method is used for mapping calendar to calendar DTO
     * @param calendar
     * @return calendar DTO
     */
    @Mapping(target = "minTime", expression = "java(mappingService.findMinTime(calendar.getId()))")
    @Mapping(target = "maxTime", expression = "java(mappingService.findMaxTime(calendar.getId()))")
    public abstract CalendarDTO calendarToCalendarDTO(Calendar calendar);


    /**
     * This method is used for mapping calendar to calendar with events DTO
     * @param calendar
     * @return calendar with events DTO
     */
    public abstract CalendarWithEventsDTO calendarToCalendarWithEventsDTO(Calendar calendar);
}
