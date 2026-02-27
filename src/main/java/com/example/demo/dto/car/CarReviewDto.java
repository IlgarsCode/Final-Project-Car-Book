package com.example.demo.dto.car;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CarReviewDto {
    private Long id;
    private String fullName;
    private String email;
    private String message;
    private Integer rating;
    private String photoUrl;
    private LocalDateTime createdAt;
}
