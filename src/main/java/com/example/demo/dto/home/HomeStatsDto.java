package com.example.demo.dto.home;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HomeStatsDto {
    private long totalCars;         // active cars
    private long totalOrders;       // all orders (istəsən active/pending filtrləyərik)
    private long happyCustomers;    // active users with USER role
    private long totalTestimonials; // active testimonials
}
