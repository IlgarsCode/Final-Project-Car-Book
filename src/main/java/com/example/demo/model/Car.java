package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cars")
@Getter
@Setter
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;        // Mercedes Grand Sedan
    private String brand;        // Chevrolet, Subaru və s.
    private Double pricePerDay;
    private String imageUrl;

    @Column(unique = true)
    private String slug;

    // ✅ Car-single üçün yeni field-lər
    private Integer mileage;          // 40000
    private String transmission;      // Manual / Automatic
    private String seats;             // "5 Adults"
    private String luggage;           // "4 Bags"
    private String fuel;              // Petrol / Diesel / Hybrid

    @Column(columnDefinition = "TEXT")
    private String description;       // tab Description

    // features (3 sütun)
    @Column(columnDefinition = "TEXT")
    private String featuresCol1;      // "Airconditions\nChild Seat\nGPS"
    @Column(columnDefinition = "TEXT")
    private String featuresCol2;
    @Column(columnDefinition = "TEXT")
    private String featuresCol3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CarCategory category;

    private Boolean isActive = true;
}
