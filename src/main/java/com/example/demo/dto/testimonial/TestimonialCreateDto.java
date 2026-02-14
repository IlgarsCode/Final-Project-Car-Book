package com.example.demo.dto.testimonial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestimonialCreateDto {

    @Size(max = 60)
    private String position;

    @NotBlank
    @Size(min = 10, max = 500)
    private String comment;
}
