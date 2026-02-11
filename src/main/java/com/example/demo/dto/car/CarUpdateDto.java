package com.example.demo.dto.dashboard.car;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarUpdateDto {

    @NotBlank
    private String title;

    @NotBlank
    private String brand;

    @NotNull
    private Double pricePerDay;

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

    // şəkil ayrıca MultipartFile gələcək
}
