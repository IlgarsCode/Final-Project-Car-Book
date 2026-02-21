package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "testimonials")
@Getter
@Setter
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String position;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private String photoUrl;

    private Boolean isActive = true;
}
