package com.example.demo.dto.car;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CarDetailDto {

    private Long id;
    private String title;
    private String brand;
    private Integer year;
    private BigDecimal engineVolume;

    private String imageUrl;
    private String slug;

    private Integer mileage;
    private String transmission;
    private String seats;
    private String luggage;
    private String fuel;

    private String description;

    private String featuresCol1;
    private String featuresCol2;
    private String featuresCol3;

    private List<String> features1;
    private List<String> features2;
    private List<String> features3;

    private BigDecimal hourlyRate;
    private BigDecimal dailyRate;
    private BigDecimal monthlyLeasingRate;

    private BigDecimal baseHourlyRate;
    private BigDecimal baseDailyRate;
    private BigDecimal baseMonthlyLeasingRate;

    private BigDecimal fuelSurchargePerHour;

    private Boolean hasDiscount;
    private BigDecimal discountPercent;

    private BigDecimal displayPrice;
    private String displayUnit;
    private String selectedRate;

    private List<CarReviewDto> reviews;
    private CarReviewStatsDto reviewStats;
}
