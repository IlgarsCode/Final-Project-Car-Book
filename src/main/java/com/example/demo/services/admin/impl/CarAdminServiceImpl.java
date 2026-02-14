package com.example.demo.services.admin.impl;

import com.example.demo.dto.car.CarCreateDto;
import com.example.demo.dto.car.CarUpdateDto;
import com.example.demo.model.Car;
import com.example.demo.repository.CarCategoryRepository;
import com.example.demo.repository.CarPricingRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.CarReviewRepository;
import com.example.demo.services.admin.CarAdminService;
import com.example.demo.services.storage.FileStorageService;
import com.example.demo.util.SlugUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarAdminServiceImpl implements CarAdminService {

    private final CarRepository carRepository;
    private final CarPricingRepository carPricingRepository;
    private final CarReviewRepository carReviewRepository;
    private final CarCategoryRepository carCategoryRepository;// varsa
    private final FileStorageService fileStorageService;

    @Override
    public List<Car> getAll() {
        return carRepository.findAll().stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                .toList();
    }

    @Override
    public Car getById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));
    }

    @Override
    @Transactional
    public void create(CarCreateDto dto, MultipartFile image) {

        Car car = new Car();
        car.setTitle(dto.getTitle());
        car.setBrand(dto.getBrand());
        car.setYear(dto.getYear());
        car.setEngineVolume(dto.getEngineVolume());

        car.setMileage(dto.getMileage());
        car.setTransmission(dto.getTransmission());
        car.setSeats(dto.getSeats());
        car.setLuggage(dto.getLuggage());
        car.setFuel(dto.getFuel());

        car.setDescription(dto.getDescription());
        car.setFeaturesCol1(dto.getFeaturesCol1());
        car.setFeaturesCol2(dto.getFeaturesCol2());
        car.setFeaturesCol3(dto.getFeaturesCol3());

        car.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        // ✅ category
        if (dto.getCategoryId() != null) {
            var category = carCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category tapılmadı"));
            car.setCategory(category);
        } else {
            car.setCategory(null);
        }

        // ✅ slug auto
        String base = SlugUtil.slugify(dto.getBrand() + " " + dto.getTitle());
        String slug = base;
        int i = 2;
        while (slug.isBlank() || carRepository.existsBySlug(slug)) {
            slug = base + "-" + i++;
        }
        car.setSlug(slug);

        // ✅ image upload (optional)
        if (image != null && !image.isEmpty()) {
            String path = fileStorageService.storeCarImage(image);
            if (path != null) car.setImageUrl(path);
        }

        carRepository.save(car);
    }

    @Override
    @Transactional
    public void update(Long id, CarUpdateDto dto, MultipartFile image) {

        Car car = getById(id);

        car.setTitle(dto.getTitle());
        car.setBrand(dto.getBrand());
        car.setYear(dto.getYear());
        car.setEngineVolume(dto.getEngineVolume());

        car.setMileage(dto.getMileage());
        car.setTransmission(dto.getTransmission());
        car.setSeats(dto.getSeats());
        car.setLuggage(dto.getLuggage());
        car.setFuel(dto.getFuel());

        car.setDescription(dto.getDescription());
        car.setFeaturesCol1(dto.getFeaturesCol1());
        car.setFeaturesCol2(dto.getFeaturesCol2());
        car.setFeaturesCol3(dto.getFeaturesCol3());

        car.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        // ✅ category
        if (dto.getCategoryId() != null) {
            var category = carCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category tapılmadı"));
            car.setCategory(category);
        } else {
            car.setCategory(null);
        }

        // ✅ slug yenilə
        String base = SlugUtil.slugify(dto.getBrand() + " " + dto.getTitle());
        String slug = base;
        int i = 2;
        while (slug.isBlank() || (carRepository.existsBySlug(slug) && !slug.equals(car.getSlug()))) {
            slug = base + "-" + i++;
        }
        car.setSlug(slug);

        // ✅ image dəyişmək istəyirsə
        if (image != null && !image.isEmpty()) {
            fileStorageService.deleteIfExists(car.getImageUrl());
            String path = fileStorageService.storeCarImage(image);
            if (path != null) car.setImageUrl(path);
        }

        carRepository.save(car);
    }

    @Override
    @Transactional
    public void delete(Long id) { // soft
        Car car = getById(id);
        car.setIsActive(false);
        carRepository.save(car);
    }

    @Override
    @Transactional
    public void hardDelete(Long id) {
        Car car = getById(id);

        // (opsional) şəkli sil
        fileStorageService.deleteIfExists(car.getImageUrl());

        // DB-də FK-lar ON DELETE CASCADE olduğu üçün bu bəs edir
        carRepository.deleteById(id);

    }
}
