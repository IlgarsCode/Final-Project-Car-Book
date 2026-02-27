package com.example.demo.dto.car;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarCategoryCreateDto {

    @NotBlank(message = "Name boş ola bilməz")
    private String name;
}
