package com.example.demo.services;

import com.example.demo.dto.car.CarReviewCreateDto;
import com.example.demo.dto.car.CarReviewDto;

import java.util.List;

public interface CarReviewService {
    void create(String carSlug, CarReviewCreateDto form);

    List<CarReviewDto> getActiveReviewsByCarSlug(String carSlug);

    long countActiveByCarSlug(String carSlug);
}