package com.example.demo.dto.pricing;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarPricingUpsertDto {

    @NotNull
    private Long carId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal hourlyRate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal dailyRate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal monthlyLeasingRate;

    // optional
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal fuelSurchargePerHour;
}
