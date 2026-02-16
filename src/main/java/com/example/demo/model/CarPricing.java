package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    // ✅ NEW
    @Column(name = "discount_active", nullable = false)
    private Boolean discountActive = false;

    // 0..100
    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal discountPercent;

    // ---- old getters (qalsın)
    public BigDecimal getPerHourRate() { return hourlyRate; }
    public BigDecimal getPerDayRate()  { return dailyRate; }
    public BigDecimal getLeasingPerMonth() { return monthlyLeasingRate; }

    // ✅ Effective rates (endirim tətbiq olunur; fuel surcharge endirimə düşmür)
    public BigDecimal getEffectiveHourlyRate() {
        return applyDiscount(hourlyRate);
    }

    public BigDecimal getEffectiveDailyRate() {
        return applyDiscount(dailyRate);
    }

    public BigDecimal getEffectiveMonthlyLeasingRate() {
        return applyDiscount(monthlyLeasingRate);
    }

    public boolean hasDiscount() {
        return Boolean.TRUE.equals(discountActive)
                && discountPercent != null
                && discountPercent.compareTo(BigDecimal.ZERO) > 0;
    }

    private BigDecimal applyDiscount(BigDecimal base) {
        if (base == null) return BigDecimal.ZERO;
        if (!hasDiscount()) return base;

        // base * (100 - p) / 100
        BigDecimal p = discountPercent;
        BigDecimal factor = BigDecimal.valueOf(100)
                .subtract(p)
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

        return base.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
