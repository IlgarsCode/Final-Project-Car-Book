package com.example.demo.dto.car;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarDetailDto {
    private Long id;
    private String title;
    private String brand;
    private Double pricePerDay;
    private String imageUrl;
    private String slug;

    private Integer mileage;
    private String transmission;
    private String seats;
    private String luggage;
    private String fuel;

    private String description;

    private String featuresCol1;
    private String featuresCol2;
    private String featuresCol3;
}
