package com.rstejskalprojects.reservationsystem.service;

import com.rstejskalprojects.reservationsystem.model.Location;

public interface LocationService {

    Location findLocationByName(String name);

    Location findLocationById(Long id);

    Location saveLocation(Location location);
}
