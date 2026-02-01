package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "car_reviews")
@Getter
@Setter
public class CarReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // hansı maşına aiddir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    private String fullName;
    private String email;

    @Column(columnDefinition = "TEXT")
    private String message;

    // 1..5
    private Integer rating;

    // optional (sən istəsən sonra doldurarsan)
    private String photoUrl;

    private LocalDateTime createdAt;

    private Boolean isActive = true;
}
