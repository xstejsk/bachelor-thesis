package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.api.controller.model.UpdateEventRequest;
import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;
import com.rstejskalprojects.reservationsystem.service.EventService;
import com.rstejskalprojects.reservationsystem.util.customexception.EventNotFoundException;
import com.rstejskalprojects.reservationsystem.util.customexception.InvalidEventTimeException;
import com.rstejskalprojects.reservationsystem.util.customexception.MaximumCapacityException;
import com.rstejskalprojects.reservationsystem.util.customexception.OverlappingEventException;
import com.rstejskalprojects.reservationsystem.util.customexception.RecurrenceGroupNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/events", produces="application/json")
@RequiredArgsConstructor
@Slf4j
public class EventsController {

    private final EventService eventsService;

    @PostMapping("/create")
    @Operation(summary = "Create a new event", responses = {
        @ApiResponse(description = "Event was successfully created", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "409", description = "Event overlaps with an existing event.", content = @Content),
        @ApiResponse(responseCode = "401", description = "Authentication Failure", content = @Content(schema = @Schema(hidden = true))) })
    public ResponseEntity<?> saveEvents(@RequestBody @Valid EventDTO eventDTO) {
        log.info("requested to save eventDTO: {}", eventDTO);
        try{
                List<EventDTO> savedEvents = eventsService.saveEvent(eventDTO).stream()
                        .map(EventDTO::new).toList();
                String message;
                if (savedEvents.size() == 1){
                    message = "Event was successfully created";
                } else {
                    message = "Recurrent event was successfully created";
                }
                return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (OverlappingEventException e) {
            log.warn("overlapping event exception: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("error saving eventDTO: {}", eventDTO, e);
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents(@RequestParam(required=false) Long locationId) {
        log.info("requested to get all events");
        try {
            if (locationId != null) {
                return new ResponseEntity<>(eventsService.findByLocationId(locationId).stream()
                        .map(EventDTO::new)
                        .collect(Collectors.toList()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(eventsService.findAll().stream()
                        .map(EventDTO::new)
                        .collect(Collectors.toList()), HttpStatus.OK);
            }
        }catch (Exception e){
            log.error("error getting all events", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<String> cancelEvent(@PathVariable("eventId") Long id) {
        try {
            eventsService.deleteEvent(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidEventTimeException e) {
          return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EventNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{eventId}")
    public ResponseEntity<String> updateEvent(@PathVariable("eventId") Long eventId, @Valid @RequestBody UpdateEventRequest updateEventRequest) {
        try {
            eventsService.updateEvent(eventId, updateEventRequest);
            return new ResponseEntity<>("Event was successfully updated", HttpStatus.OK);
        } catch (EventNotFoundException | RecurrenceGroupNotFoundException e) {
            log.warn("event not found exception: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (OverlappingEventException | MaximumCapacityException e) {
            log.warn("updating event exception", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.warn("error updating event: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-recurrent/{groupId}")
    public ResponseEntity<String> updateRecurrenceGroup(@PathVariable("groupId") Long groupId, @Valid @RequestBody UpdateEventRequest updateEventRequest) {
        try {
            eventsService.updateRecurrentEvents(groupId, updateEventRequest);
            return new ResponseEntity<>("Recurrent event was successfully updated", HttpStatus.OK);
        } catch (RecurrenceGroupNotFoundException e) {
            log.warn("recurrence group not found exception: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (OverlappingEventException | MaximumCapacityException e) {
            log.warn("overlapping event exception: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/delete-recurrent/{groupId}")
    public ResponseEntity<String> cancelRecurrenceGroup(@PathVariable("groupId") Long groupId) {
        try {
            eventsService.deleteRecurrentEvents(groupId);
            return new ResponseEntity<>("Recurrent event was successfully canceled", HttpStatus.OK);
        } catch (RecurrenceGroupNotFoundException e) {
            log.warn("recurrence group not found exception: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
