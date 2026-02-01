package com.example.demo.services.impl;

import com.example.demo.dto.car.CarReviewCreateDto;
import com.example.demo.dto.car.CarReviewDto;
import com.example.demo.model.CarReview;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.CarReviewRepository;
import com.example.demo.services.CarReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarReviewServiceImpl implements CarReviewService {

    private final CarRepository carRepository;
    private final CarReviewRepository carReviewRepository;

    @Override
    public void create(String carSlug, CarReviewCreateDto form) {
        var car = carRepository.findBySlugAndIsActiveTrue(carSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        CarReview r = new CarReview();
        r.setCar(car);
        r.setFullName(form.getFullName());
        r.setRating(form.getRating());
        r.setMessage(form.getMessage());
        r.setCreatedAt(LocalDateTime.now());
        r.setIsActive(true);

        carReviewRepository.save(r);
    }

    @Override
    public List<CarReviewDto> getActiveReviewsByCarSlug(String carSlug) {
        var car = carRepository.findBySlugAndIsActiveTrue(carSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        return carReviewRepository.findAllByCar_IdAndIsActiveTrueOrderByCreatedAtDesc(car.getId())
                .stream()
                .map(x -> {
                    CarReviewDto dto = new CarReviewDto();
                    dto.setId(x.getId());
                    dto.setFullName(x.getFullName());
                    dto.setRating(x.getRating());
                    dto.setMessage(x.getMessage());
                    dto.setCreatedAt(x.getCreatedAt());
                    return dto;
                })
                .toList();
    }

    @Override
    public long countActiveByCarSlug(String carSlug) {
        var car = carRepository.findBySlugAndIsActiveTrue(carSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        return carReviewRepository.countByCar_IdAndIsActiveTrue(car.getId());
    }
}
