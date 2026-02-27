package com.example.demo.dto.segment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarSegmentCreateDto {
    @NotBlank(message = "Name boş ola bilməz")
    private String name;
}
