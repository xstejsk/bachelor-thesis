package com.rstejskalprojects.reservationsystem.api.controller;

import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.service.LocationService;
import com.rstejskalprojects.reservationsystem.util.customexception.LocationAlreadyExistsException;
import com.rstejskalprojects.reservationsystem.util.customexception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping
    public ResponseEntity<List<Location>> getAll(HttpServletRequest request, HttpServletResponse response) {
        List<Location> locations = locationService.findAll().stream().toList();

        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Location> saveLocation(@RequestBody Location location) {
        log.info("requested to save eventDTO: {}", location);
        try {
            return new ResponseEntity<>(locationService.saveLocation(location), HttpStatus.CREATED);
        } catch (LocationAlreadyExistsException e) {
            log.warn("location already exists exception: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.warn("error saving location: {}", location, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{locationId}")
    public ResponseEntity<String> deleteLocation(@PathVariable Long locationId) {
        log.info("requested to delete location: {}", locationId);
        try {
            locationService.deleteLocationById(locationId);
            return new ResponseEntity<>("The location with id: " + locationId +
                    "has been deleted along with corresponding events", HttpStatus.OK);
        } catch (LocationNotFoundException e){
            log.warn("location not found exception: {}", e.getMessage());
            return new ResponseEntity<>("The location with id: " + locationId + " does not exist", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("illegal argument exception: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.warn("error deleting location: {}", locationId, e);
            return new ResponseEntity<>("The location with id: " + locationId + " could not be deleted", HttpStatus.BAD_REQUEST);
        }
    }
}
