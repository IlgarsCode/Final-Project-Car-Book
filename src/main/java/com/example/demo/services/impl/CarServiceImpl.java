package com.example.demo.services.impl;

import com.example.demo.dto.car.CarListDto;
import com.example.demo.model.Car;
import com.example.demo.repository.CarRepository;
import com.example.demo.services.CarService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
}