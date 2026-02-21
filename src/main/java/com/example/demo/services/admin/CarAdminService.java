package com.example.demo.services.admin;

import com.example.demo.dto.car.CarCreateDto;
import com.example.demo.dto.car.CarUpdateDto;
import com.example.demo.model.Car;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarAdminService {
    List<Car> getAll();
    Car getById(Long id);

    void create(CarCreateDto dto, MultipartFile image);
    void update(Long id, CarUpdateDto dto, MultipartFile image);

    void delete(Long id);
    void hardDelete(Long id);
}
