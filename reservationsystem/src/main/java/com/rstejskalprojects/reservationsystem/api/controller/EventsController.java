package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;
import com.rstejskalprojects.reservationsystem.service.EventServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/events", produces="application/json")
@RequiredArgsConstructor
public class EventsController {

    private final EventServiceImpl eventsService;

    @GetMapping("/all")
    public ResponseEntity<List<EventDTO>> getAll(HttpServletRequest request, HttpServletResponse response) {
        List<EventDTO> events = eventsService.findAll().stream().map(EventDTO::new).collect(Collectors.toList());

        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<EventDTO> saveEvent(@RequestBody EventDTO eventDTO) {
        System.out.println(eventDTO);
        eventsService.saveEvent(eventDTO);
        return new ResponseEntity<>(eventDTO, HttpStatus.CREATED);
    }
}
