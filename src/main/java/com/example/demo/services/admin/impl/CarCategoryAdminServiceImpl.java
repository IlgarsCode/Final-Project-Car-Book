package com.example.demo.services.admin.impl;

import com.example.demo.dto.car.CarCategoryCreateDto;
import com.example.demo.dto.car.CarCategoryDashboardRowDto;
import com.example.demo.dto.car.CarCategoryUpdateDto;
import com.example.demo.model.CarCategory;
import com.example.demo.repository.CarCategoryRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.services.admin.CarCategoryAdminService;
import com.example.demo.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarCategoryAdminServiceImpl implements CarCategoryAdminService {

    private final CarCategoryRepository carCategoryRepository;
    private final CarRepository carRepository;

    @Override
    public List<CarCategoryDashboardRowDto> getAllRows() {
        // repository view-dən carCount götürürük
        return carCategoryRepository.findAllWithActiveCarCount()
                .stream()
                .map(v -> {
                    CarCategoryDashboardRowDto dto = new CarCategoryDashboardRowDto();
                    dto.setId(v.getId());
                    dto.setName(v.getName());
                    dto.setSlug(v.getSlug());
                    dto.setActiveCarCount(v.getCarCount());
                    return dto;
                })
                .sorted(Comparator.comparing(CarCategoryDashboardRowDto::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    public CarCategory getById(Long id) {
        return carCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category tapılmadı"));
    }

    @Override
    public void create(CarCategoryCreateDto dto) {
        String name = clean(dto.getName());

        if (carCategoryRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu adla category artıq var");
        }

        String slug = uniqueSlug(SlugUtil.slugify(name), null);

        CarCategory cc = new CarCategory();
        cc.setName(name);
        cc.setSlug(slug);

        carCategoryRepository.save(cc);
    }

    @Override
    public void update(Long id, CarCategoryUpdateDto dto) {
        CarCategory cc = getById(id);

        String name = clean(dto.getName());

        // ad dəyişibsə duplicate yoxla
        if (!cc.getName().equalsIgnoreCase(name) && carCategoryRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu adla category artıq var");
        }

        cc.setName(name);

        // slug-u da name-ə görə yenilə (istəsən sabit saxlayarıq, amma admin panel üçün bu daha məntiqlidir)
        String newBase = SlugUtil.slugify(name);
        String newSlug = uniqueSlug(newBase, cc.getSlug());
        cc.setSlug(newSlug);

        carCategoryRepository.save(cc);
    }

    @Override
    public void delete(Long id) {
        CarCategory cc = getById(id);

        // Bu category-də car varsa silmək olmaz (yoxsa FK constraint və ya data itir)
        long activeCars = carRepository.countByCategory_IdAndIsActiveTrue(cc.getId());
        long allCars = carRepository.countByCategory_Id(cc.getId());

        if (allCars > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bu category-də car var (" + allCars + "). Əvvəl cars-ı başqa category-yə keçir və ya sil."
            );
        }

        carCategoryRepository.delete(cc);
    }

    private String uniqueSlug(String base, String currentSlug) {
        String slug = base;
        int i = 2;

        while (slug.isBlank() || (carCategoryRepository.existsBySlug(slug) && !slug.equals(currentSlug))) {
            slug = base + "-" + i++;
        }

        return slug;
    }

    private static String clean(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " ");
    }
}
