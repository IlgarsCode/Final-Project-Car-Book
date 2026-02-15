package com.example.demo.services.impl;

import com.example.demo.dto.testimonial.*;
import com.example.demo.model.Testimonial;
import com.example.demo.repository.TestimonialRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.TestimonialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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

        t.setFullName(user.getFullName() != null ? user.getFullName().trim() : user.getEmail());
        t.setPhotoUrl(user.getPhotoUrl());

        t.setPosition(dto.getPosition() != null ? dto.getPosition().trim() : null);
        t.setComment(dto.getComment().trim());
        t.setIsActive(true);

        testimonialRepository.save(t);
    }

    // =========================
    // ✅ ADMIN
    // =========================

    @Override
    public Page<TestimonialAdminListDto> adminGet(int page, int size, String q, Boolean active) {
        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50));
        var p = testimonialRepository.adminSearch(q != null ? q.trim() : null, active, pageable);

        return p.map(t -> {
            TestimonialAdminListDto d = new TestimonialAdminListDto();
            d.setId(t.getId());
            d.setFullName(t.getFullName());
            d.setPosition(t.getPosition());
            d.setComment(t.getComment());
            d.setPhotoUrl(t.getPhotoUrl());
            d.setIsActive(t.getIsActive());
            return d;
        });
    }

    @Override
    public TestimonialAdminUpdateDto adminGetEditForm(Long id) {
        var t = testimonialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Testimonial tapılmadı"));

        TestimonialAdminUpdateDto dto = new TestimonialAdminUpdateDto();
        dto.setFullName(t.getFullName());
        dto.setPosition(t.getPosition());
        dto.setComment(t.getComment());
        dto.setPhotoUrl(t.getPhotoUrl());
        dto.setIsActive(t.getIsActive());
        return dto;
    }

    @Override
    public void adminUpdate(Long id, TestimonialAdminUpdateDto dto) {
        var t = testimonialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Testimonial tapılmadı"));

        t.setFullName(dto.getFullName() != null ? dto.getFullName().trim() : t.getFullName());
        t.setPosition(dto.getPosition() != null ? dto.getPosition().trim() : null);
        t.setComment(dto.getComment() != null ? dto.getComment().trim() : t.getComment());

        // photoUrl boşdursa köhnə qalsın
        if (dto.getPhotoUrl() != null && !dto.getPhotoUrl().isBlank()) {
            t.setPhotoUrl(dto.getPhotoUrl().trim());
        }

        if (dto.getIsActive() != null) {
            t.setIsActive(dto.getIsActive());
        }

        testimonialRepository.save(t);
    }

    @Override
    public void adminSetActive(Long id, boolean active) {
        var t = testimonialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Testimonial tapılmadı"));
        t.setIsActive(active);
        testimonialRepository.save(t);
    }

    @Override
    public void adminDelete(Long id) {
        var t = testimonialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Testimonial tapılmadı"));
        testimonialRepository.delete(t);
    }
}
