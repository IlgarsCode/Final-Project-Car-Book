package com.example.demo.dto.car;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarCreateDto {

    @NotBlank
    private String title;

    @NotBlank
    private String brand;

    @Min(value = 1900, message = "Year 1900-dən aşağı ola bilməz")
    @Max(value = 2100, message = "Year çox böyükdür")
    private Integer year;

    @DecimalMin(value = "0.1", message = "Engine volume 0.1-dən böyük olmalıdır")
    @DecimalMax(value = "8.0", message = "Engine volume maksimum 8.0 ola bilər")
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

    // category hələ əlavə etmirik (sənin dediyin kimi)
}
