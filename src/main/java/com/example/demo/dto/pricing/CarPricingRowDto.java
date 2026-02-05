package com.example.demo.dto.pricing;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarPricingRowDto {
    private Long carId;
    private String carTitle;
    private String carImageUrl;
    private String carSlug;

    private BigDecimal hourlyRate;
    private BigDecimal dailyRate;
    private BigDecimal monthlyLeasingRate;

    private BigDecimal fuelSurchargePerHour;

    private Double averageRating;
    private long reviewCount;
}
