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

    // ⚠️ artıq UI üçün bunu istifadə etmə.
    // İstəsən sonradan tam silərik, hələlik saxlayıram ki, mapping qırılmasın.
    private Double pricePerDay;

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

    // ✅ Pricing (DB: car_pricings)
    private BigDecimal hourlyRate;
    private BigDecimal dailyRate;
    private BigDecimal monthlyLeasingRate;
    private BigDecimal fuelSurchargePerHour;

    // ✅ Car detaildə göstəriləcək seçilmiş qiymət
    private BigDecimal displayPrice; // hourly/daily/leasing-ə görə
    private String displayUnit;      // "/per hour" | "/per day" | "/per month"
    private String selectedRate;     // "HOURLY" | "DAILY" | "LEASING"

    // Reviews
    private List<CarReviewDto> reviews;
    private CarReviewStatsDto reviewStats;
}
