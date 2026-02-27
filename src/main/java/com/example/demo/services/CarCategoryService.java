package com.example.demo.services;

import com.example.demo.dto.car.CarCategorySidebarDto;

import java.util.List;

public interface CarCategoryService {
    List<CarCategorySidebarDto> getSidebarCategories();
}
