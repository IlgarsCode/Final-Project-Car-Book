package com.example.demo.services;

import com.example.demo.dto.testimonial.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TestimonialService {

    List<TestimonialListDto> getActiveTestimonials();

    void create(String userEmail, TestimonialCreateDto dto);

    Page<TestimonialAdminListDto> adminGet(int page, int size, String q, Boolean active);
    TestimonialAdminUpdateDto adminGetEditForm(Long id);
    void adminUpdate(Long id, TestimonialAdminUpdateDto dto);
    void adminSetActive(Long id, boolean active);
    void adminDelete(Long id);
}
