package com.example.demo.dto.checkout;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CheckoutCreateDto {

    @NotNull(message = "Pick-up location seçilməlidir")
    private Long pickupLocationId;

    @NotNull(message = "Drop-off location seçilməlidir")
    private Long dropoffLocationId;

    @NotNull
    @FutureOrPresent(message = "Pickup date bu gün və ya gələcək olmalıdır")
    private LocalDate pickupDate;

    @NotNull
    @FutureOrPresent(message = "Dropoff date bu gün və ya gələcək olmalıdır")
    private LocalDate dropoffDate;

    private String pickupTime;   // "HH:mm"
    private String dropoffTime;  // "HH:mm"
}
