package com.example.demo.dto.car;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarReviewAdminFilterDto {
    private Long carId;        // dropdowndan seçiləcək
    private Boolean active;    // null / true / false
    private Integer rating;    // null / 1..5
    private String q;          // search text
}
