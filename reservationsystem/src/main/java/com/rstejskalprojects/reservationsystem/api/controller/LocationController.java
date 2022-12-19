package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@RestController
@RequestMapping(path = "/api/locations", produces="application/json")
@RequiredArgsConstructor
@Slf4j
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/all")
    public ResponseEntity<List<Location>> getAll(HttpServletRequest request, HttpServletResponse response) {
        List<Location> locations = locationService.findAll().stream().toList();

        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Location> saveLocation(@RequestBody Location location) {
        log.info("requested to save eventDTO: {}", location);
        return new ResponseEntity<>(locationService.saveLocation(location), HttpStatus.CREATED);
    }
}
