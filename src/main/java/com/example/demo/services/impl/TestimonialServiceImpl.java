package com.example.demo.services.impl;

import com.example.demo.dto.testimonial.TestimonialCreateDto;
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
        return dto;
    }

    @Override
    public void create(TestimonialCreateDto dto) {
        Testimonial t = new Testimonial();
        t.setFullName(dto.getFullName().trim());
        t.setPosition(dto.getPosition() != null ? dto.getPosition().trim() : null);
        t.setComment(dto.getComment().trim());
        t.setIsActive(true); // dərhal görünsün
        testimonialRepository.save(t);
    }
}

