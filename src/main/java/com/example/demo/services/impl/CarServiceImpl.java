package com.example.demo.services.impl;

import com.example.demo.dto.car.CarDetailDto;
import com.example.demo.dto.car.CarListDto;
import com.example.demo.dto.car.CarReviewDto;
import com.example.demo.dto.car.CarReviewStatsDto;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.CarReviewRepository;
import com.example.demo.services.CarService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarReviewRepository carReviewRepository;
    private final ModelMapper modelMapper;

    // =======================
    // CAR LIST
    // =======================
    @Override
    public List<CarListDto> getActiveCars() {
        return carRepository.findAllByIsActiveTrueOrderByIdDesc()
                .stream()
                .map(car -> modelMapper.map(car, CarListDto.class))
                .toList();
    }

    // =======================
    // CAR DETAIL (SINGLE)
    // =======================
    @Override
    public CarDetailDto getCarDetailBySlug(String slug) {

        var car = carRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı")
                );

        CarDetailDto dto = modelMapper.map(car, CarDetailDto.class);

        // ---------- REVIEWS ----------
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

        // ---------- REVIEW STATS ----------
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

    // =======================
    // RELATED CARS
    // =======================
    @Override
    public List<CarListDto> getRelatedCars(Long currentCarId, int limit) {
        return carRepository.findAllByIsActiveTrueOrderByIdDesc()
                .stream()
                .filter(c -> !c.getId().equals(currentCarId))
                .limit(limit)
                .map(c -> modelMapper.map(c, CarListDto.class))
                .toList();
    }

    @Override
    public Object getCarDetail(String slug) {
        return null;
    }
}
