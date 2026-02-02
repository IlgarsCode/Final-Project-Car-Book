package com.example.demo.services.impl;

import com.example.demo.dto.car.CarDetailDto;
import com.example.demo.dto.car.CarListDto;
import com.example.demo.dto.car.CarReviewDto;
import com.example.demo.dto.car.CarReviewStatsDto;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<Long, BigDecimal> dailyMap = new HashMap<>();

        if (!carIds.isEmpty()) {
            var pricings = carPricingRepository.findActiveByCarIds(carIds);
            for (var cp : pricings) {
                dailyMap.put(cp.getCar().getId(), safe(cp.getDailyRate()));
            }
        }

        return cars.stream()
                .map(car -> {
                    CarListDto dto = new CarListDto();
                    dto.setId(car.getId());
                    dto.setTitle(car.getTitle());
                    dto.setBrand(car.getBrand());
                    dto.setImageUrl(car.getImageUrl());
                    dto.setSlug(car.getSlug());

                    dto.setDailyRate(dailyMap.getOrDefault(car.getId(), BigDecimal.ZERO));
                    return dto;
                })
                .toList();
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

        // ✅ pricing (car_pricings)
        var pricingOpt = carPricingRepository.findActiveByCarSlug(slug);

        BigDecimal hourly = BigDecimal.ZERO;
        BigDecimal daily = BigDecimal.ZERO;
        BigDecimal leasing = BigDecimal.ZERO;
        BigDecimal surcharge = BigDecimal.ZERO;

        if (pricingOpt.isPresent()) {
            var cp = pricingOpt.get();
            hourly = safe(cp.getHourlyRate());
            daily = safe(cp.getDailyRate());
            leasing = safe(cp.getMonthlyLeasingRate());
            surcharge = safe(cp.getFuelSurchargePerHour());
        }

        dto.setHourlyRate(hourly);
        dto.setDailyRate(daily);
        dto.setMonthlyLeasingRate(leasing);
        dto.setFuelSurchargePerHour(surcharge);

        // ✅ UI üçün display price
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

        // ✅ Reviews
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
        Map<Long, BigDecimal> dailyMap = new HashMap<>();

        if (!ids.isEmpty()) {
            var pricings = carPricingRepository.findActiveByCarIds(ids);
            for (var cp : pricings) {
                dailyMap.put(cp.getCar().getId(), safe(cp.getDailyRate()));
            }
        }

        return cars.stream().map(car -> {
            CarListDto dto = new CarListDto();
            dto.setId(car.getId());
            dto.setTitle(car.getTitle());
            dto.setBrand(car.getBrand());
            dto.setImageUrl(car.getImageUrl());
            dto.setSlug(car.getSlug());
            dto.setDailyRate(dailyMap.getOrDefault(car.getId(), BigDecimal.ZERO));
            return dto;
        }).toList();
    }

    @Override
    public Object getCarDetail(String slug) {
        return null;
    }

    private static BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
