package com.example.demo.services;

import java.time.LocalDate;

public interface AvailabilityService {
    boolean isCarAvailable(Long carId, LocalDate pickupDate, LocalDate dropoffDate);
}
