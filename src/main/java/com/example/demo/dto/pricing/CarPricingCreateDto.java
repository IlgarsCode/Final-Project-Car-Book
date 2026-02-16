package com.example.demo.dto.pricing;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class CarPricingCreateDto {

    @NotNull private Long carId;

    @NotNull @PositiveOrZero private BigDecimal hourlyRate;
    @NotNull @PositiveOrZero private BigDecimal dailyRate;
    @NotNull @PositiveOrZero private BigDecimal monthlyLeasingRate;

    @PositiveOrZero private BigDecimal fuelSurchargePerHour;

    // ✅ discount
    @NotNull
    private Boolean discountActive = false;

    // 0..100
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "100.0", inclusive = true)
    private BigDecimal discountPercent;
}