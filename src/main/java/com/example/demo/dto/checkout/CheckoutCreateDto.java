package com.example.demo.dto.checkout;

import jakarta.validation.constraints.FutureOrPresent;
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
    @FutureOrPresent(message = "Pickup date bu gün və ya gələcək olmalıdır")
    private LocalDate pickupDate;

    @NotNull
    @FutureOrPresent(message = "Dropoff date bu gün və ya gələcək olmalıdır")
    private LocalDate dropoffDate;

    private String pickupTime;   // "HH:mm"
    private String dropoffTime;  // "HH:mm"

}
