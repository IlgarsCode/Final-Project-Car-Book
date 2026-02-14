package com.example.demo.services;

import com.example.demo.dto.testimonial.TestimonialCreateDto;
import com.example.demo.dto.testimonial.TestimonialListDto;

import java.util.List;

public interface TestimonialService {

    List<TestimonialListDto> getActiveTestimonials();

    // ✅ fullname və avatarı logged-in user-dan götürəcəyik
    void create(String userEmail, TestimonialCreateDto dto);
}
