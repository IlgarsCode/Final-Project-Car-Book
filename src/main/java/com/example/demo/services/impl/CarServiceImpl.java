package com.example.demo.services.impl;

import com.example.demo.dto.car.CarDetailDto;
import com.example.demo.dto.car.CarListDto;
import com.example.demo.repository.CarRepository;
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
    private final ModelMapper modelMapper;

    @Override
    public List<CarListDto> getActiveCars() {
        return carRepository.findAllByIsActiveTrueOrderByIdDesc()
                .stream()
                .map(car -> modelMapper.map(car, CarListDto.class))
                .toList();
    }

    @Override
    public CarDetailDto getCarDetailBySlug(String slug) {
        var car = carRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        return modelMapper.map(car, CarDetailDto.class);
    }

    @Override
    public List<CarListDto> getRelatedCars(Long currentCarId, int limit) {
        // sadə variant: son aktiv maşınlardan götür, cari maşını çıxart, limitlə
        return carRepository.findAllByIsActiveTrueOrderByIdDesc()
                .stream()
                .filter(c -> !c.getId().equals(currentCarId))
                .limit(limit)
                .map(c -> modelMapper.map(c, CarListDto.class))
                .toList();
    }
}
