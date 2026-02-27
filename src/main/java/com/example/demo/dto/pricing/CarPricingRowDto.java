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

    private BigDecimal baseHourlyRate;
    private BigDecimal baseDailyRate;
    private BigDecimal baseMonthlyLeasingRate;

    private BigDecimal fuelSurchargePerHour;

    private Boolean hourlyHasDiscount;
    private BigDecimal hourlyDiscountPercent;

    private Boolean dailyHasDiscount;
    private BigDecimal dailyDiscountPercent;

    private Boolean leasingHasDiscount;
    private BigDecimal leasingDiscountPercent;

    private Double averageRating;
    private long reviewCount;
}
