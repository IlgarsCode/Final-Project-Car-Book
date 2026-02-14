package com.example.demo.services.impl;

import com.example.demo.dto.testimonial.TestimonialCreateDto;
import com.example.demo.dto.testimonial.TestimonialListDto;
import com.example.demo.model.Testimonial;
import com.example.demo.repository.TestimonialRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.TestimonialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestimonialServiceImpl implements TestimonialService {

    private final TestimonialRepository testimonialRepository;
    private final UserRepository userRepository;

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

    @Override
    public void create(String userEmail, TestimonialCreateDto dto) {

        var user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));

        Testimonial t = new Testimonial();

        // ✅ Fullname artıq formdan gəlmir
        t.setFullName(user.getFullName() != null ? user.getFullName().trim() : user.getEmail());

        // ✅ Avatar da user-dan
        t.setPhotoUrl(user.getPhotoUrl());

        t.setPosition(dto.getPosition() != null ? dto.getPosition().trim() : null);
        t.setComment(dto.getComment().trim());
        t.setIsActive(true);

        testimonialRepository.save(t);
    }
}
