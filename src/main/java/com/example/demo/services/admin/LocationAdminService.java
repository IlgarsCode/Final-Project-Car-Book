package com.example.demo.services.admin;

import com.example.demo.dto.location.LocationCreateDto;
import com.example.demo.dto.location.LocationUpdateDto;
import com.example.demo.model.Location;

import java.util.List;

public interface LocationAdminService {
    List<Location> getAll();
    Location getById(Long id);

    void create(LocationCreateDto dto);
    void update(Long id, LocationUpdateDto dto);

    void softDelete(Long id);
    void activate(Long id);
}
