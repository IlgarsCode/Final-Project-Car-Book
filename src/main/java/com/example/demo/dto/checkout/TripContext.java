package com.example.demo.dto.checkout;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class TripContext implements Serializable {
    private Long pickupLoc;
    private Long dropoffLoc;
    private LocalDate pickupDate;
    private LocalDate dropoffDate;
}
