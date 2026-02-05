package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "car_pricings")
@Getter
@Setter
public class CarPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false, unique = true)
    private Car car;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal hourlyRate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal dailyRate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyLeasingRate;


    @Column(precision = 12, scale = 2)
    private BigDecimal fuelSurchargePerHour;

    private Boolean isActive = true;

    public BigDecimal getPerHourRate() {
        return hourlyRate;
    }

    public BigDecimal getPerDayRate() {
        return dailyRate;
    }

    public BigDecimal getLeasingPerMonth() {
        return monthlyLeasingRate;
    }
}
