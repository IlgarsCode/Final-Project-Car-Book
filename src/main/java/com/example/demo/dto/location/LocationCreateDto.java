package com.example.demo.dto.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationCreateDto {

    @NotBlank(message = "Name boş ola bilməz")
    private String name;

    @NotNull(message = "Sort order boş ola bilməz")
    private Integer sortOrder = 0;

    private Boolean isActive = true;
}
