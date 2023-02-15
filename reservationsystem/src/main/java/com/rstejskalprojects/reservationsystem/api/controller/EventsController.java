package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.api.controller.model.UpdateEventRequest;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;
import com.rstejskalprojects.reservationsystem.service.EventService;
import com.rstejskalprojects.reservationsystem.util.customexception.EventNotFoundException;
import com.rstejskalprojects.reservationsystem.util.customexception.OverlappingEventException;
import com.rstejskalprojects.reservationsystem.util.customexception.RecurrenceGroupNotFoundException;
import com.rstejskalprojects.reservationsystem.util.customexception.UrlParamsEnum;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/events", produces="application/json")
@RequiredArgsConstructor
@Slf4j
public class EventsController {

    private final EventService eventsService;
    private final Set<String> allowedParams = new HashSet<>(List.of("locationName", "locationId"));


    @PostMapping("/create")
    @Operation(summary = "Get thing", responses = {
        @ApiResponse(description = "Event was successfully created", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "409", description = "Event overlaps with an existing event.", content = @Content),
        @ApiResponse(responseCode = "401", description = "Authentication Failure", content = @Content(schema = @Schema(hidden = true))) })
    public ResponseEntity<List<EventDTO>> saveEvents(@RequestBody EventDTO eventDTO) {
        log.info("requested to save eventDTO: {}", eventDTO);
        try{
                List<EventDTO> savedEvents = eventsService.saveEvent(eventDTO).stream()
                .map(EventDTO::new)
                .collect(Collectors.toList());
            return new ResponseEntity<>(savedEvents, HttpStatus.CREATED);
        } catch (OverlappingEventException e) {
            log.warn("overlapping event exception: {}", e.getMessage());
            return new ResponseEntity<>(e.getOverlappingEvents(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("error saving eventDTO: {}", eventDTO, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAll(@RequestParam(required=false) Map<String,String> params) {
        log.info("get all events called with parameters {}", params);
        if (!allowedParams.containsAll(params.keySet())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Event> events;

        if (params.containsKey(UrlParamsEnum.LOCATION_NAME.getValue())) {
            events = eventsService.findByLocationName(params.get(UrlParamsEnum.LOCATION_NAME.getValue()));
        } else if (params.containsKey(UrlParamsEnum.LOCATION_ID.getValue())) {
            try {
                events = eventsService.findByLocationId(Long.parseLong(params.get(UrlParamsEnum.LOCATION_ID.getValue())));
            } catch (Exception e) {
                log.info("could not parse locationId to long");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            events = eventsService.findAll();
        }
        List<EventDTO> eventDTOS = events.stream().map(EventDTO::new).collect(Collectors.toList());

        return new ResponseEntity<>(eventDTOS, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<EventDTO>> getNonCanceled(@RequestParam(required=false) Map<String,String> params) {
        log.info("get all events called with parameters {}", params);
        if (!allowedParams.containsAll(params.keySet())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Event> events;

        if (params.containsKey(UrlParamsEnum.LOCATION_NAME.getValue())) {
            events = eventsService.findByLocationName(params.get(UrlParamsEnum.LOCATION_NAME.getValue()));
        } else if (params.containsKey(UrlParamsEnum.LOCATION_ID.getValue())) {
            try {
                events = eventsService.findByLocationId(Long.parseLong(params.get(UrlParamsEnum.LOCATION_ID.getValue())));
            } catch (Exception e) {
                log.info("could not parse locationId to long");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            events = eventsService.findAllNonCanceled();
        }
        List<EventDTO> eventDTOS = events.stream().map(EventDTO::new).collect(Collectors.toList());

        return new ResponseEntity<>(eventDTOS, HttpStatus.OK);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<String> cancelEvent(@PathVariable("id") Long id) {
        try {
            Event event = eventsService.findEventById(id);
            eventsService.cancelEvent(event);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EventNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{eventId}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable("eventId") Long eventId, @RequestBody UpdateEventRequest updateEventRequest) {
        try {
            Event updatedEvent = eventsService.updateEvent(eventId, updateEventRequest);
            return new ResponseEntity<>(new EventDTO(updatedEvent), HttpStatus.OK);
        } catch (EventNotFoundException | RecurrenceGroupNotFoundException e) {
            log.warn("event not found exception: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (OverlappingEventException e) {
            log.warn("overlapping event exception: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.warn("error updating event: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/recurrent/{recurrenceGroupId}")
    public ResponseEntity<List<EventDTO>> updateRecurrenceGroup(@PathVariable("recurrenceGroupId") Long recurrenceGroupId, @RequestBody UpdateEventRequest updateEventRequest) {
        try {
            List<Event> updatedEvents = eventsService.updateRecurrentEvents(recurrenceGroupId, updateEventRequest);
            List<EventDTO> eventDTOS = updatedEvents.stream().map(EventDTO::new).toList();
            return new ResponseEntity<>(eventDTOS, HttpStatus.OK);
        } catch (RecurrenceGroupNotFoundException e) {
            log.warn("recurrence group not found exception: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (OverlappingEventException e) {
            log.warn("overlapping event exception: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/cancel/group/{id}")
    public ResponseEntity<String> cancelRecurrenceGroup(@PathVariable("id") Long groupId) {
        List<Event> events = eventsService.cancelRecurrentEvents(groupId);
        if (events.isEmpty()) {
            return new ResponseEntity<>("recurrence group with id " + groupId + "does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
