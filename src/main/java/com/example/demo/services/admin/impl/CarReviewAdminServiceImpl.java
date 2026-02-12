package com.example.demo.services.admin.impl;

import com.example.demo.dto.car.CarReviewAdminFilterDto;
import com.example.demo.dto.car.CarReviewDashboardDto;
import com.example.demo.model.CarReview;
import com.example.demo.repository.CarReviewRepository;
import com.example.demo.services.admin.CarReviewAdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CarReviewAdminServiceImpl implements CarReviewAdminService {

    private final CarReviewRepository carReviewRepository;

    @Override
    public Page<CarReviewDashboardDto> getPage(CarReviewAdminFilterDto filter, int page, int size) {
        if (filter == null) filter = new CarReviewAdminFilterDto();

        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 5), 50));

        return carReviewRepository.adminSearch(
                        filter.getCarId(),
                        filter.getActive(),
                        filter.getRating(),
                        filter.getQ(),
                        pageable
                )
                .map(this::toDashboardDto);
    }

    @Override
    public CarReviewDashboardDto getById(Long id) {
        var r = carReviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review tapılmadı"));
        return toDashboardDto(r);
    }

    @Override
    @Transactional
    public void toggleActive(Long id) {
        CarReview r = carReviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review tapılmadı"));

        r.setIsActive(r.getIsActive() == null ? true : !r.getIsActive());
        carReviewRepository.save(r);
    }

    @Override
    @Transactional
    public void hardDelete(Long id) {
        if (!carReviewRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review tapılmadı");
        }
        carReviewRepository.deleteById(id);
    }

    private CarReviewDashboardDto toDashboardDto(CarReview r) {
        CarReviewDashboardDto dto = new CarReviewDashboardDto();
        dto.setId(r.getId());
        dto.setFullName(r.getFullName());
        dto.setCarBrand(r.getCar().getBrand());
        dto.setEmail(r.getEmail());
        dto.setMessage(r.getMessage());
        dto.setRating(r.getRating());
        dto.setIsActive(r.getIsActive());
        dto.setCreatedAt(r.getCreatedAt());

        // car lazy-dir, amma burada controller view render edəcək — transaction xaricində N+1 risk var.
        // Ona görə adminSearch query join ilə gəlir, problem olmur.
        dto.setCarId(r.getCar().getId());
        dto.setCarTitle(r.getCar().getTitle());
        dto.setCarSlug(r.getCar().getSlug());
        return dto;
    }
}