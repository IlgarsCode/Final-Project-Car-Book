package com.example.demo.services.admin;

import com.example.demo.dto.segment.CarSegmentCreateDto;
import com.example.demo.dto.segment.CarSegmentDashboardRowDto;
import com.example.demo.dto.segment.CarSegmentUpdateDto;
import com.example.demo.model.CarSegment;

import java.util.List;

public interface CarSegmentAdminService {

    List<CarSegmentDashboardRowDto> getAllRows();
    CarSegment getById(Long id);

    void create(CarSegmentCreateDto dto);
    void update(Long id, CarSegmentUpdateDto dto);
    void delete(Long id);
}
