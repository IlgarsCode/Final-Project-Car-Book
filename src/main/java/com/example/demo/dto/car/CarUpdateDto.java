package com.example.demo.dto.car;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarUpdateDto {

    @NotBlank
    private String title;

    @NotBlank
    private String brand;

    @NotNull(message = "Segment seçilməlidir")
    private Long segmentId;

    @Min(1900)
    @Max(2100)
    private Integer year;

    @DecimalMin("0.1")
    @DecimalMax("8.0")
    private BigDecimal engineVolume;

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

}
