package com.example.demo.services;

import com.example.demo.dto.car.CarDetailDto;
import com.example.demo.dto.car.CarListDto;
import com.example.demo.dto.enums.PricingRateType;

import java.util.List;

public interface CarService {

    List<CarListDto> getActiveCars();
    List<CarListDto> getActiveCars(String categorySlug);

    // ✅ NEW
    List<CarListDto> getActiveCars(String categorySlug, String segmentSlug);

    CarDetailDto getCarDetailBySlug(String slug);
    CarDetailDto getCarDetailBySlug(String slug, PricingRateType rateType);

    List<CarListDto> getRelatedCars(Long currentCarId, int limit);
}
