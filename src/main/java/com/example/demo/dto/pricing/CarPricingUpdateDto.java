package com.example.demo.dto.pricing;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarPricingUpdateDto {

    @NotNull
    private Long carId;

    @NotNull @PositiveOrZero
    private BigDecimal hourlyRate;

    @NotNull @PositiveOrZero
    private BigDecimal dailyRate;

    @NotNull @PositiveOrZero
    private BigDecimal monthlyLeasingRate;

    @PositiveOrZero
    private BigDecimal fuelSurchargePerHour; // optional
}