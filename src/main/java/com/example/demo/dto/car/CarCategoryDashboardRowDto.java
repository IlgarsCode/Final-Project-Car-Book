package com.example.demo.dto.car;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarCategoryDashboardRowDto {
    private Long id;
    private String name;
    private String slug;
    private long activeCarCount;
}
