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
    private String imageUrl;
    private String slug;

    // ✅ pricing-dən gələcək
    private BigDecimal hourlyRate;
    private BigDecimal dailyRate;
    private BigDecimal monthlyLeasingRate;
}
