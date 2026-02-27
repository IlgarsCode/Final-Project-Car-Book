package com.example.demo.dto.car;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarReviewAdminFilterDto {
    private Long carId;
    private Boolean active;
    private Integer rating;
    private String q;
}
