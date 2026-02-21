package com.example.demo.model;

import com.example.demo.dto.enums.PricingRateType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_cart_car_rate",
                columnNames = {"cart_id", "car_id", "rate_type"}
        )
)
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Enumerated(EnumType.STRING)
    @Column(name = "rate_type", nullable = false, length = 16)
    private PricingRateType rateType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPriceSnapshot;

    @Column(precision = 12, scale = 2)
    private BigDecimal baseUnitPriceSnapshot;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentSnapshot;

    @Column(precision = 12, scale = 2)
    private BigDecimal fuelSurchargePerHourSnapshot;

    @Column(nullable = false)
    private Integer unitCount = 1;

    private Integer quantity = 1;

    private LocalDateTime addedAt = LocalDateTime.now();
}
