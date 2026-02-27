package com.example.demo.services.admin;

import com.example.demo.dto.pricing.CarPricingCreateDto;
import com.example.demo.dto.pricing.CarPricingUpdateDto;
import com.example.demo.model.CarPricing;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;

public interface CarPricingAdminService {
    List<CarPricing> getAllActiveRows();
    CarPricing getByCarId(Long carId);

    void createForCar(Long carId,
                      BigDecimal hourly,
                      BigDecimal daily,
                      BigDecimal leasing,
                      BigDecimal fuelSurcharge);

    void updateForCar(Long carId,
                      BigDecimal hourly,
                      BigDecimal daily,
                      BigDecimal leasing,
                      BigDecimal fuelSurcharge);

    void createOrUpdate(@Valid CarPricingCreateDto dto);
    void update(CarPricingUpdateDto dto);
    void deactivate(Long carId);
}
