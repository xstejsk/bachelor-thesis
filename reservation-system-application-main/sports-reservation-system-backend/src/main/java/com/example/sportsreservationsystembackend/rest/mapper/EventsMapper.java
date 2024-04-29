package com.example.sportsreservationsystembackend.rest.mapper;

import com.example.sportsreservationsystembackend.model.Event;
import com.example.sportsreservationsystembackend.model.RecurrenceGroup;
import com.xstejsk.reservationapp.main.rest.model.CreateEventRequest;
import com.xstejsk.reservationapp.main.rest.model.EventDTO;
import com.xstejsk.reservationapp.main.rest.model.Recurrence;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;

/**
 * This class represents events mapper
 *
 * @author Radim Stejskal
 */

@Mapper(componentModel = "spring")
@AllArgsConstructor
@Component
public abstract class EventsMapper {

    /**
     * This method is used for mapping EventDTO to Event
     * @param eventDTO
     * @return Event object
     */
    @Mapping(expression = "java(getRecurrenceGroup(eventDTO.getRecurrence()))", target = "recurrenceGroup")
    public abstract Event eventDTOToEvent(EventDTO eventDTO);

    /**
     * This method is used for mapping CreateEventRequest to Event
     * @param createEventRequest
     * @return Event object
     */

    @Mapping(expression = "java(getRecurrenceGroup(createEventRequest.getRecurrence()))", target = "recurrenceGroup")
    public abstract Event createEventRequestToEvent(CreateEventRequest createEventRequest);

    /**
     * This method is used for mapping Event to EventDTO
     * @param event
     * @return EventDTO object
     */
    @Mapping(expression = "java(getRecurrence(event))", target = "recurrence")
    public abstract EventDTO eventToEventDTO(Event event);

    /**
     * This method is used for mapping Recurrence from Event to Recurrence from EventDTO
     * @param event
     * @return
     */
    public Recurrence getRecurrence(Event event) {
        Recurrence recurrence = new Recurrence();
        if (event.getRecurrenceGroup() != null) {
            recurrence.setDaysOfWeek(event.getRecurrenceGroup().getDaysOfWeek().stream().map(DayOfWeek::getValue).toList());
            recurrence.setRepeatUntil(event.getRecurrenceGroup().getRepeatUntil().toString());
        }
        return recurrence;
    }

    /**
     * This method is used for mapping Recurrence from EventDTO to RecurrenceGroup from Event
     * @param recurrence
     * @return
     */
    public RecurrenceGroup getRecurrenceGroup(Recurrence recurrence) {
        if (recurrence != null) {
            RecurrenceGroup recurrenceGroup = new RecurrenceGroup();
            recurrenceGroup.setDaysOfWeek(recurrence.getDaysOfWeek().stream().map(DayOfWeek::of).toList());
            recurrenceGroup.setRepeatUntil(java.time.LocalDate.parse(recurrence.getRepeatUntil()));
            return recurrenceGroup;
        }
        return null;
    }
}
