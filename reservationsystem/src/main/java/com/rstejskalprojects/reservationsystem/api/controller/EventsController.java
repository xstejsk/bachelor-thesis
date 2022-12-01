package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.service.EventServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(path = "/api/events", produces="application/json")
@RequiredArgsConstructor
public class EventsController {

    private final EventServiceImpl eventsService;

    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAll(HttpServletRequest request, HttpServletResponse response) {
        List<Event> events = eventsService.findAll();

        return new ResponseEntity<>(events, HttpStatus.OK);
    }
}
