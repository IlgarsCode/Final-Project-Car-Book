package com.example.demo.services;

import com.example.demo.dto.pricing.CarPricingRowDto;

import java.time.LocalDate;
import java.util.List;

public interface PricingService {

    List<CarPricingRowDto> getPricingRows(String categorySlug);

    List<CarPricingRowDto> getPricingRows(String categorySlug, LocalDate pickupDate, LocalDate dropoffDate);
    List<CarPricingRowDto> getPricingRows(String categorySlug, String segmentSlug);

    // ✅ NEW: segment + date
    List<CarPricingRowDto> getPricingRows(String categorySlug, String segmentSlug, LocalDate pickupDate, LocalDate dropoffDate);
}

