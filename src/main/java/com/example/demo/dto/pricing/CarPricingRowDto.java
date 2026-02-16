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

    // ✅ effective (endirimli)
    private BigDecimal hourlyRate;
    private BigDecimal dailyRate;
    private BigDecimal monthlyLeasingRate;

    // ✅ base (endirimdən əvvəl) - UI üçün
    private BigDecimal baseHourlyRate;
    private BigDecimal baseDailyRate;
    private BigDecimal baseMonthlyLeasingRate;

    private BigDecimal fuelSurchargePerHour;

    private Boolean hasDiscount;
    private BigDecimal discountPercent;

    private Double averageRating;
    private long reviewCount;
}
