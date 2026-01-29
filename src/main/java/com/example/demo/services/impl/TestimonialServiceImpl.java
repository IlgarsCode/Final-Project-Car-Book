package com.example.demo.services.impl;

import com.example.demo.dto.testimonial.TestimonialListDto;
import com.example.demo.model.Testimonial;
import com.example.demo.repository.TestimonialRepository;
import com.example.demo.services.TestimonialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestimonialServiceImpl implements TestimonialService {

    private final TestimonialRepository testimonialRepository;

    @Override
    public List<TestimonialListDto> getActiveTestimonials() {
        return testimonialRepository.findAllByIsActiveTrue()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private TestimonialListDto mapToDto(Testimonial t) {
        TestimonialListDto dto = new TestimonialListDto();
        dto.setId(t.getId());
        dto.setFullName(t.getFullName());
        dto.setPosition(t.getPosition());
        dto.setComment(t.getComment());
        dto.setPhotoUrl(t.getPhotoUrl());
        return dto;
    }
}

