package com.example.demo.services.impl;

import com.example.demo.dto.pricing.CarPricingRowDto;
import com.example.demo.repository.CarPricingRepository;
import com.example.demo.repository.CarReviewRepository;
import com.example.demo.services.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final CarPricingRepository carPricingRepository;
    private final CarReviewRepository carReviewRepository;

    @Override
    public List<CarPricingRowDto> getActivePricingRows() {

        var rows = carPricingRepository.findActivePricingRows();

        // 1) carId-ləri topla
        List<Long> carIds = rows.stream()
                .map(x -> x.getCar().getId())
                .distinct()
                .toList();

        // 2) avg map
        Map<Long, Double> avgMap = carReviewRepository.findAverageRatingsByCarIds(carIds)
                .stream()
                .collect(Collectors.toMap(
                        CarReviewRepository.CarAvgView::getCarId,
                        v -> v.getAvgRating() != null ? v.getAvgRating() : 0.0
                ));

        // 3) count map
        Map<Long, Long> countMap = carReviewRepository.countActiveReviewsByCarIds(carIds)
                .stream()
                .collect(Collectors.toMap(
                        CarReviewRepository.CarCountView::getCarId,
                        CarReviewRepository.CarCountView::getReviewCount
                ));

        // 4) DTO list
        return rows.stream()
                .map(cp -> {
                    var car = cp.getCar();
                    Long carId = car.getId();

                    CarPricingRowDto dto = new CarPricingRowDto();

                    // ===== CAR INFO =====
                    dto.setCarId(carId);
                    dto.setCarTitle(car.getTitle());
                    dto.setCarImageUrl(car.getImageUrl());
                    dto.setCarSlug(car.getSlug());

                    // ===== PRICING (CarPricing entity-dən) =====
                    dto.setHourlyRate(cp.getHourlyRate());
                    dto.setDailyRate(cp.getDailyRate());
                    dto.setMonthlyLeasingRate(cp.getMonthlyLeasingRate());
                    dto.setFuelSurchargePerHour(cp.getFuelSurchargePerHour());

                    // ===== RATING (reviews-dan) =====
                    dto.setAverageRating(avgMap.getOrDefault(carId, 0.0));
                    dto.setReviewCount(countMap.getOrDefault(carId, 0L));

                    return dto;
                })
                .toList();
    }
}