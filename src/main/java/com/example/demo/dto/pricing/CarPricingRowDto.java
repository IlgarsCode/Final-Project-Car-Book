package com.example.demo.dto.pricing;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarPricingRowDto {
    private Long carId;
    private String carTitle;     // "Cheverolet SUV Car"
    private String carImageUrl;  // car.imageUrl
    private String carSlug;      // details link üçün

    private BigDecimal hourlyRate;
    private BigDecimal dailyRate;
    private BigDecimal monthlyLeasingRate;

    private BigDecimal fuelSurchargePerHour;

    private Double averageRating;
    private long reviewCount;
}
