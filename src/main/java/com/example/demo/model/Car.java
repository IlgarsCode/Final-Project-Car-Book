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
    private String imageUrl;

    @Column(unique = true)
    private String slug;
    private Integer mileage;
    private String transmission;
    private String seats;
    private String luggage;
    private String fuel;

    @Column(columnDefinition = "TEXT")
    private String description;


    @Column(columnDefinition = "TEXT")
    private String featuresCol1;
    @Column(columnDefinition = "TEXT")
    private String featuresCol2;
    @Column(columnDefinition = "TEXT")
    private String featuresCol3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CarCategory category;

    private Boolean isActive = true;
}
