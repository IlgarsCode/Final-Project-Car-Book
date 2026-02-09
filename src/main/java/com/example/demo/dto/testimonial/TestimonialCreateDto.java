package com.example.demo.dto.testimonial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestimonialCreateDto {

    @NotBlank
    @Size(min = 3, max = 60)
    private String fullName;

    @Size(max = 60)
    private String position;

    @NotBlank
    @Size(min = 10, max = 500)
    private String comment;

    @Size(max = 500)
    private String photoUrl; // optional
}
