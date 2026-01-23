package com.example.demo.dto.car;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CarListDto {

    private Long id;
    private String name;
    private String title;
    private String brand;
    private Double pricePerDay;
    private String imageUrl;
    private String slug;
}