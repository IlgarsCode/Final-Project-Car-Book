package com.example.demo.dto.car;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarCategorySidebarDto {
    private Long id;
    private String name;
    private String slug;
    private long carCount;
}
