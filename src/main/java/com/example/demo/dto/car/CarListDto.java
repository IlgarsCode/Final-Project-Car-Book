package com.example.demo.dto.car;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarListDto {

    private Long id;
    private String title;
    private String brand;
    private Integer year;
    private BigDecimal engineVolume;
    private String imageUrl;
    private String slug;

    private BigDecimal hourlyRate;
    private BigDecimal dailyRate;
    private BigDecimal monthlyLeasingRate;

    private BigDecimal baseHourlyRate;
    private BigDecimal baseDailyRate;
    private BigDecimal baseMonthlyLeasingRate;

    private Boolean hasDiscount;
    private BigDecimal discountPercent;
}
