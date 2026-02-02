package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(name = "uk_cart_car", columnNames = {"cart_id", "car_id"}))
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

    // snapshot: sifariş zamanı qiymət dəyişsə belə cart-da sabit qalsın
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal dailyRateSnapshot;

    // İstəsən, eyni maşını bir neçə ədəd götürmək (praktikada rental-da çox lazım olmur)
    private Integer quantity = 1;

    private LocalDateTime addedAt = LocalDateTime.now();
}
