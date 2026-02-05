package com.example.demo.model;

import com.example.demo.dto.enums.PricingRateType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(nullable = false)
    private String carTitleSnapshot;

    private String carImageUrlSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PricingRateType rateType; // HOURLY/DAILY/LEASING

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPriceSnapshot; // seçilmiş paketə görə: hourly/daily/leasing

    // hourly üçün lazım olur (istəsən digərlərində 0 saxla)
    @Column(precision = 12, scale = 2)
    private BigDecimal fuelSurchargePerHourSnapshot;

    // neçə vahid: hourly-> neçə saat, daily-> neçə gün, leasing-> neçə ay
    @Column(nullable = false)
    private Integer unitCountSnapshot;

    private Integer quantity = 1;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;
}
