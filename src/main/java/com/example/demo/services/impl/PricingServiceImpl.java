package com.example.demo.services.impl;

import com.example.demo.dto.pricing.CarPricingRowDto;
import com.example.demo.model.CarPricing;
import com.example.demo.repository.CarPricingRepository;
import com.example.demo.repository.CarReviewRepository;
import com.example.demo.services.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final CarPricingRepository carPricingRepository;
    private final CarReviewRepository carReviewRepository;

    @Override
    public List<CarPricingRowDto> getPricingRows(String categorySlug) {

        List<CarPricing> rows =
                (categorySlug == null || categorySlug.isBlank())
                        ? carPricingRepository.findActivePricingRows()
                        : carPricingRepository.findActivePricingRowsByCategorySlug(categorySlug);

        Map<Long, Long> countMap = new HashMap<>();
        Map<Long, Double> avgMap = new HashMap<>();

        for (CarPricing cp : rows) {
            var car = cp.getCar();
            Long carId = car.getId();

            long total = carReviewRepository.countByCar_IdAndIsActiveTrue(carId);
            countMap.put(carId, total);

            if (total == 0) {
                avgMap.put(carId, 0.0);
                continue;
            }

            long c1 = carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(carId, 1);
            long c2 = carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(carId, 2);
            long c3 = carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(carId, 3);
            long c4 = carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(carId, 4);
            long c5 = carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(carId, 5);

            double avg = (1.0 * c1 + 2.0 * c2 + 3.0 * c3 + 4.0 * c4 + 5.0 * c5) / total;
            avgMap.put(carId, avg);
        }

        return rows.stream()
                .map(cp -> {
                    var car = cp.getCar();
                    Long carId = car.getId();

                    CarPricingRowDto dto = new CarPricingRowDto();
                    dto.setCarId(carId);
                    dto.setCarTitle(car.getTitle());
                    dto.setCarImageUrl(car.getImageUrl());
                    dto.setCarSlug(car.getSlug());

                    dto.setHourlyRate(cp.getHourlyRate());
                    dto.setDailyRate(cp.getDailyRate());
                    dto.setMonthlyLeasingRate(cp.getMonthlyLeasingRate());

                    dto.setFuelSurchargePerHour(cp.getFuelSurchargePerHour());

                    dto.setAverageRating(avgMap.getOrDefault(carId, 0.0));
                    dto.setReviewCount(countMap.getOrDefault(carId, 0L));

                    return dto;
                })
                .toList();
    }
}
