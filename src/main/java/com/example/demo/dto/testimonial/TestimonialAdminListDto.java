package com.example.demo.dto.testimonial;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestimonialAdminListDto {
    private Long id;
    private String fullName;
    private String position;
    private String comment;
    private String photoUrl;
    private Boolean isActive;
}
