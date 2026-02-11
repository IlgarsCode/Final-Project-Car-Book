package com.example.demo.dto.car;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarCreateDto {

    @NotBlank
    private String title;

    @NotBlank
    private String brand;

    private Integer mileage;
    private String transmission;
    private String seats;
    private String luggage;
    private String fuel;

    private String description;

    private String featuresCol1;
    private String featuresCol2;
    private String featuresCol3;

    private Boolean isActive = true;

    private Long categoryId;

    // category hələ əlavə etmirik (sənin dediyin kimi)
}
