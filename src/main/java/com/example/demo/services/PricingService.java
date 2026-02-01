package com.example.demo.services;

import com.example.demo.dto.pricing.CarPricingRowDto;

import java.util.List;

public interface PricingService {
    List<CarPricingRowDto> getPricingRows(String categorySlug);
}
