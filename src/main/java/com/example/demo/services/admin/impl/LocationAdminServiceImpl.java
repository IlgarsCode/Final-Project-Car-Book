package com.example.demo.services.admin.impl;

import com.example.demo.dto.location.LocationCreateDto;
import com.example.demo.dto.location.LocationUpdateDto;
import com.example.demo.model.Location;
import com.example.demo.repository.LocationRepository;
import com.example.demo.services.admin.LocationAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationAdminServiceImpl implements LocationAdminService {

    private final LocationRepository locationRepository;

    @Override
    public List<Location> getAll() {
        return locationRepository.findAll().stream()
                .sorted(Comparator
                        .comparing((Location l) -> l.getSortOrder() == null ? 0 : l.getSortOrder())
                        .thenComparing(Location::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    public Location getById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location tapılmadı"));
    }

    @Override
    public void create(LocationCreateDto dto) {
        String name = clean(dto.getName());

        if (locationRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu adla location artıq var");
        }

        Location l = new Location();
        l.setName(name);
        l.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        l.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        locationRepository.save(l);
    }

    @Override
    public void update(Long id, LocationUpdateDto dto) {
        Location l = getById(id);
        String name = clean(dto.getName());

        if (!l.getName().equalsIgnoreCase(name) && locationRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu adla location artıq var");
        }

        l.setName(name);
        l.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        l.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        locationRepository.save(l);
    }

    @Override
    public void softDelete(Long id) {
        Location l = getById(id);
        l.setIsActive(false);
        locationRepository.save(l);
    }

    @Override
    public void activate(Long id) {
        Location l = getById(id);
        l.setIsActive(true);
        locationRepository.save(l);
    }

    private static String clean(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ");
    }
}
