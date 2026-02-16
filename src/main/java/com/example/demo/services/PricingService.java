package com.example.demo.services;

import com.example.demo.dto.pricing.CarPricingRowDto;

import java.time.LocalDate;
import java.util.List;

public interface PricingService {

    // əvvəlki kimi (tarix yoxdursa hamısını ver)
    List<CarPricingRowDto> getPricingRows(String categorySlug);

    // ✅ tarixlə filtr
    List<CarPricingRowDto> getPricingRows(String categorySlug, LocalDate pickupDate, LocalDate dropoffDate);
}
