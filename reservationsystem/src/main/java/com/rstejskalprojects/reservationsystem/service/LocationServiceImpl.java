package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.repository.LocationRepository;
import com.rstejskalprojects.reservationsystem.util.customexception.LocationAlreadyExistsException;
import com.rstejskalprojects.reservationsystem.util.customexception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public Location findLocationByName(String name) {
        return locationRepository.findByName(name).orElseThrow(() ->
                new LocationNotFoundException(String.format("location of name %s does not exist", name)));
    }

    @Override
    public Location findLocationById(Long id) {
        return locationRepository.findById(id).orElseThrow(() ->
                new LocationNotFoundException(String.format("location of id %s does not exist", id)));
    }

    @Override
    public Location saveLocation(Location location) {
        if (location.getName().isBlank()) {
            throw new IllegalArgumentException("location name cannot be blank");
        }
        locationRepository.findByName(location.getName()).ifPresent(location1 -> {
            throw new LocationAlreadyExistsException(String.format("location of name %s already exists", location.getName()));
        });
        log.info("saving location: {}", location);
        return locationRepository.save(location);
    }

    @Override
    public List<Location> findAll() {
        return locationRepository.findAll();
    }
}
