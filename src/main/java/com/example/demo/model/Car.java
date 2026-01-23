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

    private Boolean isActive = true;
}

