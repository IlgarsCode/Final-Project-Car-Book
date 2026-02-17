package com.example.demo.services.impl;

import com.example.demo.dto.pricing.CarPricingRowDto;
import com.example.demo.model.CarPricing;
import com.example.demo.model.OrderStatus;
import com.example.demo.repository.CarPricingRepository;
import com.example.demo.repository.CarReviewRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.services.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final CarPricingRepository carPricingRepository;
    private final CarReviewRepository carReviewRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<CarPricingRowDto> getPricingRows(String categorySlug) {
        return getPricingRows(categorySlug, null, null, null);
    }

    @Override
    public List<CarPricingRowDto> getPricingRows(String categorySlug, LocalDate pickupDate, LocalDate dropoffDate) {
        return getPricingRows(categorySlug, null, pickupDate, dropoffDate);
    }

    @Override
    public List<CarPricingRowDto> getPricingRows(String categorySlug, String segmentSlug) {
        return getPricingRows(categorySlug, segmentSlug, null, null);
    }

    @Override
    public List<CarPricingRowDto> getPricingRows(String categorySlug, String segmentSlug, LocalDate pickupDate, LocalDate dropoffDate) {

        boolean hasCat = categorySlug != null && !categorySlug.isBlank();
        boolean hasSeg = segmentSlug != null && !segmentSlug.isBlank();

        List<CarPricing> rows =
                (!hasCat && !hasSeg)
                        ? carPricingRepository.findActivePricingRows()
                        : (hasCat && !hasSeg)
                        ? carPricingRepository.findActivePricingRowsByCategorySlug(categorySlug)
                        : (!hasCat)
                        ? carPricingRepository.findActivePricingRowsBySegmentSlug(segmentSlug)
                        : carPricingRepository.findActivePricingRowsByCategoryAndSegment(categorySlug, segmentSlug);

        if (rows == null || rows.isEmpty()) return List.of();

        // ✅ availability filter
        if (pickupDate != null && dropoffDate != null) {
            if (dropoffDate.isBefore(pickupDate)) return List.of();

            List<Long> carIds = rows.stream()
                    .map(cp -> cp.getCar().getId())
                    .filter(Objects::nonNull)
                    .toList();

            List<OrderStatus> blocking = List.of(OrderStatus.PENDING, OrderStatus.APPROVED);

            List<Long> busyIds = orderRepository.findBusyCarIdsInRange(carIds, pickupDate, dropoffDate, blocking);
            Set<Long> busy = new HashSet<>(busyIds == null ? List.of() : busyIds);

            rows = rows.stream()
                    .filter(cp -> cp.getCar() != null && !busy.contains(cp.getCar().getId()))
                    .toList();
        }

        // rating stats
        Map<Long, Long> countMap = new HashMap<>();
        Map<Long, Double> avgMap = new HashMap<>();

        for (CarPricing cp : rows) {
            Long carId = cp.getCar().getId();

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

        return rows.stream().map(cp -> {
            var car = cp.getCar();
            Long carId = car.getId();

            CarPricingRowDto dto = new CarPricingRowDto();
            dto.setCarId(carId);
            dto.setCarTitle(car.getTitle());
            dto.setCarImageUrl(car.getImageUrl());
            dto.setCarSlug(car.getSlug());

            dto.setHourlyRate(cp.getEffectiveHourlyRate());
            dto.setDailyRate(cp.getEffectiveDailyRate());
            dto.setMonthlyLeasingRate(cp.getEffectiveMonthlyLeasingRate());

            dto.setBaseHourlyRate(cp.getHourlyRate());
            dto.setBaseDailyRate(cp.getDailyRate());
            dto.setBaseMonthlyLeasingRate(cp.getMonthlyLeasingRate());

            dto.setFuelSurchargePerHour(cp.getFuelSurchargePerHour());

            dto.setHourlyHasDiscount(cp.hasHourlyDiscount());
            dto.setHourlyDiscountPercent(cp.getHourlyDiscountPercent());

            dto.setDailyHasDiscount(cp.hasDailyDiscount());
            dto.setDailyDiscountPercent(cp.getDailyDiscountPercent());

            dto.setLeasingHasDiscount(cp.hasLeasingDiscount());
            dto.setLeasingDiscountPercent(cp.getLeasingDiscountPercent());

            dto.setAverageRating(avgMap.getOrDefault(carId, 0.0));
            dto.setReviewCount(countMap.getOrDefault(carId, 0L));
            return dto;
        }).toList();
    }
}
