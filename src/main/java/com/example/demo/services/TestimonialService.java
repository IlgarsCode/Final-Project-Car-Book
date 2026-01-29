package com.example.demo.services;

import com.example.demo.dto.testimonial.TestimonialListDto;

import java.util.List;

public interface TestimonialService {

    List<TestimonialListDto> getActiveTestimonials();
}

