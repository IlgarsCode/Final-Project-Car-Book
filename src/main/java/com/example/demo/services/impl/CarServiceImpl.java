package com.example.demo.services.impl;

import com.example.demo.dto.car.*;
import com.example.demo.dto.enums.PricingRateType;
import com.example.demo.repository.CarPricingRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.CarReviewRepository;
import com.example.demo.services.CarService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarReviewRepository carReviewRepository;
    private final CarPricingRepository carPricingRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CarListDto> getActiveCars() {
        return getActiveCars(null);
    }

    @Override
    public List<CarListDto> getActiveCars(String categorySlug) {

        var cars = (categorySlug == null || categorySlug.isBlank())
                ? carRepository.findAllByIsActiveTrueOrderByIdDesc()
                : carRepository.findAllByIsActiveTrueAndCategory_SlugOrderByIdDesc(categorySlug);

        List<Long> carIds = cars.stream().map(c -> c.getId()).toList();

        Map<Long, com.example.demo.model.CarPricing> pricingMap = new HashMap<>();
        if (!carIds.isEmpty()) {
            var pricings = carPricingRepository.findActiveByCarIds(carIds);
            for (var cp : pricings) {
                if (cp.getCar() != null && cp.getCar().getId() != null) {
                    pricingMap.put(cp.getCar().getId(), cp);
                }
            }
        }

        return cars.stream().map(car -> {
            CarListDto dto = new CarListDto();
            dto.setId(car.getId());
            dto.setTitle(car.getTitle());
            dto.setBrand(car.getBrand());
            dto.setYear(car.getYear());
            dto.setEngineVolume(car.getEngineVolume());
            dto.setImageUrl(car.getImageUrl());
            dto.setSlug(car.getSlug());

            var cp = pricingMap.get(car.getId());
            if (cp != null) {

                // effective
                dto.setHourlyRate(safe(cp.getEffectiveHourlyRate()));
                dto.setDailyRate(safe(cp.getEffectiveDailyRate()));
                dto.setMonthlyLeasingRate(safe(cp.getEffectiveMonthlyLeasingRate()));

                // base
                dto.setBaseHourlyRate(safe(cp.getHourlyRate()));
                dto.setBaseDailyRate(safe(cp.getDailyRate()));
                dto.setBaseMonthlyLeasingRate(safe(cp.getMonthlyLeasingRate()));

                // ✅ car list-də hansı endirim göstərilsin?
                // adətən car list daily göstərir — ona görə daily endirimi götürürük.
                boolean hasDailyDiscount = Boolean.TRUE.equals(cp.getDailyDiscountActive())
                        && cp.getDailyDiscountPercent() != null
                        && cp.getDailyDiscountPercent().compareTo(BigDecimal.ZERO) > 0;

                dto.setHasDiscount(hasDailyDiscount);
                dto.setDiscountPercent(hasDailyDiscount ? cp.getDailyDiscountPercent() : null);

            } else {
                dto.setHourlyRate(BigDecimal.ZERO);
                dto.setDailyRate(BigDecimal.ZERO);
                dto.setMonthlyLeasingRate(BigDecimal.ZERO);
                dto.setHasDiscount(false);
                dto.setDiscountPercent(null);
            }

            return dto;
        }).toList();
    }

    @Override
    public CarDetailDto getCarDetailBySlug(String slug) {
        return getCarDetailBySlug(slug, PricingRateType.DAILY);
    }

    @Override
    public CarDetailDto getCarDetailBySlug(String slug, PricingRateType rateType) {

        var car = carRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        CarDetailDto dto = modelMapper.map(car, CarDetailDto.class);

        dto.setFeatures1(splitFeatures(car.getFeaturesCol1()));
        dto.setFeatures2(splitFeatures(car.getFeaturesCol2()));
        dto.setFeatures3(splitFeatures(car.getFeaturesCol3()));

        var pricingOpt = carPricingRepository.findActiveByCarSlug(slug);

        BigDecimal hourly = BigDecimal.ZERO, daily = BigDecimal.ZERO, leasing = BigDecimal.ZERO;
        BigDecimal baseHourly = BigDecimal.ZERO, baseDaily = BigDecimal.ZERO, baseLeasing = BigDecimal.ZERO;
        BigDecimal surcharge = BigDecimal.ZERO;

        boolean hasDiscount = false;
        BigDecimal discPercent = null;

        if (pricingOpt.isPresent()) {
            var cp = pricingOpt.get();

            // base
            baseHourly = safe(cp.getHourlyRate());
            baseDaily = safe(cp.getDailyRate());
            baseLeasing = safe(cp.getMonthlyLeasingRate());

            // effective
            hourly = safe(cp.getEffectiveHourlyRate());
            daily = safe(cp.getEffectiveDailyRate());
            leasing = safe(cp.getEffectiveMonthlyLeasingRate());

            surcharge = safe(cp.getFuelSurchargePerHour());

            // ✅ seçilmiş rateType-ə görə endirim
            switch (rateType) {
                case HOURLY -> {
                    hasDiscount = Boolean.TRUE.equals(cp.getHourlyDiscountActive())
                            && cp.getHourlyDiscountPercent() != null
                            && cp.getHourlyDiscountPercent().compareTo(BigDecimal.ZERO) > 0;
                    discPercent = hasDiscount ? cp.getHourlyDiscountPercent() : null;
                }
                case LEASING -> {
                    hasDiscount = Boolean.TRUE.equals(cp.getLeasingDiscountActive())
                            && cp.getLeasingDiscountPercent() != null
                            && cp.getLeasingDiscountPercent().compareTo(BigDecimal.ZERO) > 0;
                    discPercent = hasDiscount ? cp.getLeasingDiscountPercent() : null;
                }
                default -> { // DAILY
                    hasDiscount = Boolean.TRUE.equals(cp.getDailyDiscountActive())
                            && cp.getDailyDiscountPercent() != null
                            && cp.getDailyDiscountPercent().compareTo(BigDecimal.ZERO) > 0;
                    discPercent = hasDiscount ? cp.getDailyDiscountPercent() : null;
                }
            }
        }

        dto.setHourlyRate(hourly);
        dto.setDailyRate(daily);
        dto.setMonthlyLeasingRate(leasing);

        dto.setBaseHourlyRate(baseHourly);
        dto.setBaseDailyRate(baseDaily);
        dto.setBaseMonthlyLeasingRate(baseLeasing);

        dto.setFuelSurchargePerHour(surcharge);

        dto.setHasDiscount(hasDiscount);
        dto.setDiscountPercent(discPercent);

        dto.setSelectedRate(rateType.name());

        switch (rateType) {
            case HOURLY -> {
                dto.setDisplayPrice(hourly);
                dto.setDisplayUnit("/per hour");
            }
            case LEASING -> {
                dto.setDisplayPrice(leasing);
                dto.setDisplayUnit("/per month");
            }
            default -> {
                dto.setDisplayPrice(daily);
                dto.setDisplayUnit("/per day");
            }
        }

        var reviews = carReviewRepository
                .findAllByCar_IdAndIsActiveTrueOrderByCreatedAtDesc(car.getId())
                .stream()
                .map(r -> {
                    CarReviewDto rd = new CarReviewDto();
                    rd.setId(r.getId());
                    rd.setFullName(r.getFullName());
                    rd.setMessage(r.getMessage());
                    rd.setRating(r.getRating());
                    rd.setPhotoUrl(r.getPhotoUrl());
                    rd.setCreatedAt(r.getCreatedAt());
                    return rd;
                })
                .toList();

        dto.setReviews(reviews);

        long total = carReviewRepository.countByCar_IdAndIsActiveTrue(car.getId());
        CarReviewStatsDto stats = new CarReviewStatsDto();
        stats.setTotal(total);

        stats.setCount5(carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(car.getId(), 5));
        stats.setCount4(carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(car.getId(), 4));
        stats.setCount3(carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(car.getId(), 3));
        stats.setCount2(carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(car.getId(), 2));
        stats.setCount1(carReviewRepository.countByCar_IdAndRatingAndIsActiveTrue(car.getId(), 1));

        if (total > 0) {
            stats.setPercent5((int) Math.round(stats.getCount5() * 100.0 / total));
            stats.setPercent4((int) Math.round(stats.getCount4() * 100.0 / total));
            stats.setPercent3((int) Math.round(stats.getCount3() * 100.0 / total));
            stats.setPercent2((int) Math.round(stats.getCount2() * 100.0 / total));
            stats.setPercent1((int) Math.round(stats.getCount1() * 100.0 / total));
        } else {
            stats.setPercent5(0);
            stats.setPercent4(0);
            stats.setPercent3(0);
            stats.setPercent2(0);
            stats.setPercent1(0);
        }

        dto.setReviewStats(stats);
        return dto;
    }

    @Override
    public List<CarListDto> getRelatedCars(Long currentCarId, int limit) {

        var cars = carRepository.findAllByIsActiveTrueOrderByIdDesc()
                .stream()
                .filter(c -> !c.getId().equals(currentCarId))
                .limit(limit)
                .toList();

        List<Long> ids = cars.stream().map(c -> c.getId()).toList();
        Map<Long, com.example.demo.model.CarPricing> pricingMap = new HashMap<>();

        if (!ids.isEmpty()) {
            var pricings = carPricingRepository.findActiveByCarIds(ids);
            for (var cp : pricings) {
                if (cp.getCar() != null && cp.getCar().getId() != null) {
                    pricingMap.put(cp.getCar().getId(), cp);
                }
            }
        }

        return cars.stream().map(car -> {
            CarListDto dto = new CarListDto();
            dto.setId(car.getId());
            dto.setTitle(car.getTitle());
            dto.setBrand(car.getBrand());
            dto.setYear(car.getYear());
            dto.setEngineVolume(car.getEngineVolume());
            dto.setImageUrl(car.getImageUrl());
            dto.setSlug(car.getSlug());

            var cp = pricingMap.get(car.getId());
            if (cp != null) {

                dto.setDailyRate(safe(cp.getEffectiveDailyRate()));
                dto.setBaseDailyRate(safe(cp.getDailyRate()));

                boolean hasDailyDiscount = Boolean.TRUE.equals(cp.getDailyDiscountActive())
                        && cp.getDailyDiscountPercent() != null
                        && cp.getDailyDiscountPercent().compareTo(BigDecimal.ZERO) > 0;

                dto.setHasDiscount(hasDailyDiscount);
                dto.setDiscountPercent(hasDailyDiscount ? cp.getDailyDiscountPercent() : null);

            } else {
                dto.setDailyRate(BigDecimal.ZERO);
                dto.setHasDiscount(false);
                dto.setDiscountPercent(null);
            }
            return dto;
        }).toList();
    }

    private static List<String> splitFeatures(String s) {
        if (s == null) return List.of();
        String normalized = s.replace("\r\n", "\n").trim();
        if (normalized.isBlank()) return List.of();

        String[] parts = normalized.split("\\n|,|;");
        if (parts.length == 1) return List.of(normalized);

        return Arrays.stream(parts)
                .map(String::trim)
                .filter(x -> !x.isBlank())
                .collect(Collectors.toList());
    }

    private static BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
