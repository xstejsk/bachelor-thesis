package com.example.sportsreservationsystembackend.service;

import com.example.sportsreservationsystembackend.model.Location;
import com.xstejsk.reservationapp.main.rest.model.LocationDTO;

import java.util.List;

public interface LocationService {

    List<LocationDTO> getAll();

    Location findById(String locationId);
}
