package com.example.sportsreservationsystembackend.rest.api;

import com.example.sportsreservationsystembackend.service.LocationService;
import com.xstejsk.reservationapp.main.rest.api.LocationsApi;
import com.xstejsk.reservationapp.main.rest.model.LocationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This class represents location controller
 *
 * @author Radim Stejskal
 */

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class LocationController implements LocationsApi {

    private final LocationService locationService;

    /**
     * This method is used for getting all locations
     * @return ResponseEntity<List<LocationDTO>> object
     */

    @Override
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAll());
    }
}
