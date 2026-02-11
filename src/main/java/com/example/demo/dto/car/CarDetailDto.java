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

    private Double pricePerDay;

    private String imageUrl;
    private String slug;

    private Integer mileage;
    private String transmission;
    private String seats;
    private String luggage;
    private String fuel;

    private String description;

    // DB-də saxlanan raw text (məs: textarea-dan gələn)
    private String featuresCol1;
    private String featuresCol2;
    private String featuresCol3;

    // UI üçün parçalanmış list
    private List<String> features1;
    private List<String> features2;
    private List<String> features3;

    private BigDecimal hourlyRate;
    private BigDecimal dailyRate;
    private BigDecimal monthlyLeasingRate;
    private BigDecimal fuelSurchargePerHour;

    private BigDecimal displayPrice;
    private String displayUnit;
    private String selectedRate;

    private List<CarReviewDto> reviews;
    private CarReviewStatsDto reviewStats;
}
