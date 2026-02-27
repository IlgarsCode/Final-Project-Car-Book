package com.example.demo.dto.car;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CarReviewDashboardDto {
    private Long id;

    private Long carId;
    private String carTitle;
    private String carBrand;
    private String carSlug;

    private String fullName;
    private String email;
    private String message;
    private Integer rating;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
