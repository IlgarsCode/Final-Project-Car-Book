package com.example.demo.services.impl;

import com.example.demo.model.Location;
import com.example.demo.repository.LocationRepository;
import com.example.demo.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public List<Location> getActiveLocations() {
        return locationRepository.findAllByIsActiveTrueOrderBySortOrderAscNameAsc();
    }
}
