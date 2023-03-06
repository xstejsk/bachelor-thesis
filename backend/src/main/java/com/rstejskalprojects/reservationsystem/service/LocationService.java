package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Location;

import java.util.List;

public interface LocationService {

    Location findLocationById(Long id);

    Location saveLocation(Location location);

    List<Location> findAll();

    void deleteLocationById(Long id);
}

