package com.example.demo.services;

import com.example.demo.dto.car.CarListDto;

import java.util.List;

public interface CarService {

    List<CarListDto> getActiveCars();
}
