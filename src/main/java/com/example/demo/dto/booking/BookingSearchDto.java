package com.example.demo.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookingSearchDto {

    @NotNull
    private Long pickupLocationId;

    @NotNull
    private Long dropoffLocationId;

    @NotNull
    private LocalDate pickupDate;

    @NotNull
    private LocalDate dropoffDate;
}
