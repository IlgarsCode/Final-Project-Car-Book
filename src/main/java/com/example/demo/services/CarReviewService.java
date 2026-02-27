package com.example.demo.services;

import com.example.demo.dto.car.CarReviewCreateDto;
import com.example.demo.dto.car.CarReviewDto;
import com.example.demo.dto.car.CarReviewStatsDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CarReviewService {
    void create(String carSlug, CarReviewCreateDto form);

    void create(String carSlug, String userEmail, CarReviewCreateDto form);

    List<CarReviewDto> getActiveReviewsByCarSlug(String carSlug);

    long countActiveByCarSlug(String carSlug);

    CarReviewStatsDto getStatsByCarSlug(String carSlug);

    Page<CarReviewDto> getActiveReviewsByCarSlug(String carSlug, int page, int size);
}