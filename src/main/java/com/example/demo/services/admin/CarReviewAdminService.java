package com.example.demo.services.admin;

import com.example.demo.dto.car.CarReviewAdminFilterDto;
import com.example.demo.dto.car.CarReviewDashboardDto;
import org.springframework.data.domain.Page;

public interface CarReviewAdminService {
    Page<CarReviewDashboardDto> getPage(CarReviewAdminFilterDto filter, int page, int size);

    CarReviewDashboardDto getById(Long id);

    void toggleActive(Long id);

    void hardDelete(Long id);
}
