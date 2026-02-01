package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "car_categories")
@Getter
@Setter
public class CarCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // SUV, Sedan, Offroad və s.

    @Column(nullable = false, unique = true)
    private String slug; // suv, sedan, offroad (url üçün)
}
