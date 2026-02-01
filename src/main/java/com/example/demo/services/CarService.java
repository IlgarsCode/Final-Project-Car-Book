package com.example.demo.services;

import com.example.demo.dto.car.CarDetailDto;
import com.example.demo.dto.car.CarListDto;

import java.util.List;

public interface CarService {

    List<CarListDto> getActiveCars();
    CarDetailDto getCarDetailBySlug(String slug);

    List<CarListDto> getActiveCars(String categorySlug);

    // related cars üçün
    List<CarListDto> getRelatedCars(Long currentCarId, int limit);

    Object getCarDetail(String slug);
}
