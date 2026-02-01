package com.example.demo.services.impl;

import com.example.demo.dto.car.CarReviewCreateDto;
import com.example.demo.dto.car.CarReviewDto;
import com.example.demo.dto.car.CarReviewStatsDto;
import com.example.demo.model.CarReview;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.CarReviewRepository;
import com.example.demo.services.CarReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        // ID-ni heç yerdə set ETMƏ! (duplicate key problemi burdan çıxır)
        r.setCar(car);
        r.setFullName(form.getFullName());
        r.setEmail(form.getEmail());
        r.setRating(form.getRating());
        r.setMessage(form.getMessage());
        r.setCreatedAt(LocalDateTime.now());
        r.setIsActive(true);

        // istəsən sonra açarsan
        // r.setPhotoUrl(form.getPhotoUrl());

        carReviewRepository.save(r);
    }

    @Override
    public List<CarReviewDto> getActiveReviewsByCarSlug(String carSlug) {
        var car = carRepository.findBySlugAndIsActiveTrue(carSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        return carReviewRepository.findAllByCar_IdAndIsActiveTrueOrderByCreatedAtDesc(car.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public Page<CarReviewDto> getActiveReviewsByCarSlug(String carSlug, int page, int size) {
        var car = carRepository.findBySlugAndIsActiveTrue(carSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return carReviewRepository.findByCar_IdAndIsActiveTrue(car.getId(), pageable)
                .map(this::toDto);
    }

    @Override
    public long countActiveByCarSlug(String carSlug) {
        var car = carRepository.findBySlugAndIsActiveTrue(carSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        return carReviewRepository.countByCar_IdAndIsActiveTrue(car.getId());
    }

    @Override
    public CarReviewStatsDto getStatsByCarSlug(String carSlug) {

        var car = carRepository.findBySlugAndIsActiveTrue(carSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        Long carId = car.getId();

        long total = carReviewRepository.countByCar_IdAndIsActiveTrue(carId);

        long c5 = carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(carId, 5);
        long c4 = carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(carId, 4);
        long c3 = carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(carId, 3);
        long c2 = carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(carId, 2);
        long c1 = carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(carId, 1);

        CarReviewStatsDto dto = new CarReviewStatsDto();
        dto.setTotal(total);

        dto.setCount5(c5);
        dto.setCount4(c4);
        dto.setCount3(c3);
        dto.setCount2(c2);
        dto.setCount1(c1);

        if (total > 0) {
            dto.setPercent5((int) (c5 * 100 / total));
            dto.setPercent4((int) (c4 * 100 / total));
            dto.setPercent3((int) (c3 * 100 / total));
            dto.setPercent2((int) (c2 * 100 / total));
            dto.setPercent1((int) (c1 * 100 / total));
        } else {
            dto.setPercent5(0);
            dto.setPercent4(0);
            dto.setPercent3(0);
            dto.setPercent2(0);
            dto.setPercent1(0);
        }

        return dto;
    }

    private CarReviewDto toDto(CarReview x) {
        CarReviewDto dto = new CarReviewDto();
        dto.setId(x.getId());
        dto.setFullName(x.getFullName());
        dto.setEmail(x.getEmail());      // CarReviewDto-da varsa
        dto.setRating(x.getRating());
        dto.setMessage(x.getMessage());
        dto.setCreatedAt(x.getCreatedAt());
        dto.setPhotoUrl(x.getPhotoUrl()); // CarReviewDto-da varsa
        return dto;
    }
}
