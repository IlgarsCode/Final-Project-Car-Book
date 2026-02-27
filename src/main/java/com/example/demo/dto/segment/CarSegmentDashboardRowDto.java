package com.example.demo.dto.segment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarSegmentDashboardRowDto {
    private Long id;
    private String name;
    private String slug;
    private long activeCarCount;
}
