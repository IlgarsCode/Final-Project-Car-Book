package com.example.demo.dto.checkout;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CheckoutCreateDto {

    @NotNull
    private Long pickupLocationId;

    @NotNull
    private Long dropoffLocationId;

    @NotNull
    @FutureOrPresent
    private LocalDate pickupDate;

    @NotNull
    @FutureOrPresent
    private LocalDate dropoffDate;

    private String pickupTime;
    private String dropoffTime;
}
