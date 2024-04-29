package com.example.sportsreservationsystembackend.service.impl;

import com.example.sportsreservationsystembackend.exceptions.ResourceNotFoundException;
import com.example.sportsreservationsystembackend.model.Location;
import com.example.sportsreservationsystembackend.repository.LocationRepository;
import com.example.sportsreservationsystembackend.rest.mapper.LocationMapper;
import com.example.sportsreservationsystembackend.service.LocationService;
import com.xstejsk.reservationapp.main.rest.model.LocationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class represents location service implementation
 *
 * @Author Radim Stejskal
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    /**
     * This method returns all locations
     * @return list of LocationDTO
     */
    @Override
    public List<LocationDTO> getAll() {
        return locationRepository.findAll().stream().map(locationMapper::toDto).toList();
    }

    /**
     * This method returns location by id
     * @param locationId
     * @return LocationDTO
     */
    @Override
    public Location findById(String locationId) {
        return locationRepository.findById(locationId).orElseThrow(
                () -> new ResourceNotFoundException("Location with id: " + locationId + " not found")
        );
    }
}
