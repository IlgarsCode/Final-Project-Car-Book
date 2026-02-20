package com.example.demo.dto.car;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarReviewCreateDto {


    @NotBlank
    private String message;

    @Min(1)
    @Max(5)
    private Integer rating;
}
