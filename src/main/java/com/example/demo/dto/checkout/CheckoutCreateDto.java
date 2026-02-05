package com.example.demo.dto.checkout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CheckoutCreateDto {

    @NotBlank
    private String pickupLocation;

    @NotBlank
    private String dropoffLocation;

    @NotNull
    private LocalDate pickupDate;

    @NotNull
    private LocalDate dropoffDate;

    private String pickupTime;
    private String dropoffTime;
}
