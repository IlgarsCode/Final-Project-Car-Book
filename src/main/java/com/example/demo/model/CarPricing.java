package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "car_pricing")
@Getter
@Setter
public class CarPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false, unique = true)
    private Car car;

    @Column(nullable = false)
    private BigDecimal hourlyRate = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal dailyRate = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal monthlyLeasingRate = BigDecimal.ZERO;

    private BigDecimal fuelSurchargePerHour;

    @Column(nullable = false)
    private Boolean isActive = true;

    // ✅ NEW: separate discounts
    @Column(nullable = false)
    private Boolean hourlyDiscountActive = false;
    private BigDecimal hourlyDiscountPercent;

    @Column(nullable = false)
    private Boolean dailyDiscountActive = false;
    private BigDecimal dailyDiscountPercent;

    @Column(nullable = false)
    private Boolean leasingDiscountActive = false;
    private BigDecimal leasingDiscountPercent;

    // ==========================
    // ✅ Effective rate calculators
    // ==========================

    public BigDecimal getEffectiveHourlyRate() {
        return applyDiscount(safe(hourlyRate), hourlyDiscountActive, hourlyDiscountPercent);
    }

    public BigDecimal getEffectiveDailyRate() {
        return applyDiscount(safe(dailyRate), dailyDiscountActive, dailyDiscountPercent);
    }

    public BigDecimal getEffectiveMonthlyLeasingRate() {
        return applyDiscount(safe(monthlyLeasingRate), leasingDiscountActive, leasingDiscountPercent);
    }

    public boolean hasAnyDiscount() {
        return hasDiscount(hourlyDiscountActive, hourlyDiscountPercent)
                || hasDiscount(dailyDiscountActive, dailyDiscountPercent)
                || hasDiscount(leasingDiscountActive, leasingDiscountPercent);
    }

    public boolean hasHourlyDiscount() {
        return hasDiscount(hourlyDiscountActive, hourlyDiscountPercent);
    }

    public boolean hasDailyDiscount() {
        return hasDiscount(dailyDiscountActive, dailyDiscountPercent);
    }

    public boolean hasLeasingDiscount() {
        return hasDiscount(leasingDiscountActive, leasingDiscountPercent);
    }

    private static boolean hasDiscount(Boolean active, BigDecimal percent) {
        return Boolean.TRUE.equals(active)
                && percent != null
                && percent.compareTo(BigDecimal.ZERO) > 0;
    }

    private static BigDecimal applyDiscount(BigDecimal base, Boolean active, BigDecimal percent) {
        if (!hasDiscount(active, percent)) return base;

        // 0..100 clamp (safety)
        BigDecimal p = percent;
        if (p.compareTo(BigDecimal.ZERO) < 0) p = BigDecimal.ZERO;
        if (p.compareTo(new BigDecimal("100")) > 0) p = new BigDecimal("100");

        BigDecimal multiplier = BigDecimal.ONE.subtract(p.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP));
        BigDecimal out = base.multiply(multiplier);

        // currency-like
        return out.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
