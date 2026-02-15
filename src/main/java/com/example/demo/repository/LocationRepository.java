package com.example.demo.repository;

import com.example.demo.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByIsActiveTrueOrderBySortOrderAscNameAsc();
    boolean existsByNameIgnoreCase(String name);
}
