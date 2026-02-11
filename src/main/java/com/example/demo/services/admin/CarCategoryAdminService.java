package com.example.demo.services.admin;

import com.example.demo.dto.car.CarCategoryCreateDto;
import com.example.demo.dto.car.CarCategoryDashboardRowDto;
import com.example.demo.dto.car.CarCategoryUpdateDto;
import com.example.demo.model.CarCategory;

import java.util.List;

public interface CarCategoryAdminService {

    List<CarCategoryDashboardRowDto> getAllRows(); // list üçün (car count ilə)
    CarCategory getById(Long id);

    void create(CarCategoryCreateDto dto);
    void update(Long id, CarCategoryUpdateDto dto);
    void delete(Long id); // hard delete (çünki CarCategory-də isActive yoxdur)
}
