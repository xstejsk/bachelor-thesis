package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.repository.LocationRepository;
import com.rstejskalprojects.reservationsystem.util.customexception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
        return locationRepository.save(location);
    }
}
