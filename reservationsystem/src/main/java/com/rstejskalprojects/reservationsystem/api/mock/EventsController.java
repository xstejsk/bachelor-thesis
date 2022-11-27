package com.rstejskalprojects.reservationsystem.api.mock;

import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.FrequencyEnum;
import com.rstejskalprojects.reservationsystem.model.RecurrenceRule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/api/events", produces="application/json")
public class EventsController {

    @GetMapping("/all")
    public ResponseEntity<List<Event>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Event event = new Event(1, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 5,
                50, "Some event", "descr", false, new RecurrenceRule(FrequencyEnum.DAILY, 1, 10));
        Event event1 = new Event(2, LocalDateTime.now().plusHours(0), LocalDateTime.now().plusHours(5), 5,
                50, "Some other event", "descr", false, null);
        HttpHeaders headers = new HttpHeaders();

        ResponseEntity<List<Event>> entity = new ResponseEntity<>(List.of(event, event1),headers, HttpStatus.CREATED);

        return entity;
    }
}
