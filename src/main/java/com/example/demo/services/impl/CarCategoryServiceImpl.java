package com.example.demo.services.impl;

import com.example.demo.dto.car.CarCategorySidebarDto;
import com.example.demo.repository.CarCategoryRepository;
import com.example.demo.services.CarCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarCategoryServiceImpl implements CarCategoryService {

    private final CarCategoryRepository carCategoryRepository;

    @Override
    public List<CarCategorySidebarDto> getSidebarCategories() {
        return carCategoryRepository.findAllWithActiveCarCount()
                .stream()
                .map(x -> {
                    CarCategorySidebarDto dto = new CarCategorySidebarDto();
                    dto.setId(x.getId());
                    dto.setName(x.getName());
                    dto.setSlug(x.getSlug());
                    dto.setCarCount(x.getCarCount());
                    return dto;
                })
                .toList();
    }
}
