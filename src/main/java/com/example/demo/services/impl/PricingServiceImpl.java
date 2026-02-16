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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final CarPricingRepository carPricingRepository;
    private final CarReviewRepository carReviewRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<CarPricingRowDto> getPricingRows(String categorySlug) {
        // tarixsiz: əvvəlki davranış
        return getPricingRows(categorySlug, null, null);
    }

    @Override
    public List<CarPricingRowDto> getPricingRows(String categorySlug, LocalDate pickupDate, LocalDate dropoffDate) {

        List<CarPricing> rows =
                (categorySlug == null || categorySlug.isBlank())
                        ? carPricingRepository.findActivePricingRows()
                        : carPricingRepository.findActivePricingRowsByCategorySlug(categorySlug);

        if (rows == null || rows.isEmpty()) return List.of();

        // ✅ ƏGƏR tarixlər verilibsə → busy car-ları çıxart
        if (pickupDate != null && dropoffDate != null) {

            // səhv interval gəlibsə filtr eləmək risklidir -> hamısını göstərməyək, sadəcə boş qaytar
            if (dropoffDate.isBefore(pickupDate)) {
                return List.of();
            }

            List<Long> carIds = rows.stream()
                    .map(cp -> cp.getCar().getId())
                    .filter(Objects::nonNull)
                    .toList();

            List<OrderStatus> blocking = List.of(OrderStatus.PENDING, OrderStatus.APPROVED);

            List<Long> busyIds = orderRepository.findBusyCarIdsInRange(
                    carIds, pickupDate, dropoffDate, blocking
            );

            Set<Long> busySet = new HashSet<>(busyIds == null ? List.of() : busyIds);

            rows = rows.stream()
                    .filter(cp -> cp.getCar() != null && !busySet.contains(cp.getCar().getId()))
                    .toList();
        }

        // rating stats (qalsın)
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
