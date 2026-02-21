package com.example.demo.dto.home;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HomeStatsDto {
    private long totalCars;
    private long totalOrders;
    private long happyCustomers;
    private long totalTestimonials;
}
