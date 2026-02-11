package com.example.demo.services.admin.impl;

import com.example.demo.dto.dashboard.car.CarCreateDto;
import com.example.demo.dto.dashboard.car.CarUpdateDto;
import com.example.demo.model.Car;
import com.example.demo.model.CarPricing;
import com.example.demo.repository.CarPricingRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.services.admin.CarAdminService;
import com.example.demo.services.storage.FileStorageService;
import com.example.demo.util.SlugUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarAdminServiceImpl implements CarAdminService {

    private final CarRepository carRepository;
    private final CarPricingRepository carPricingRepository;
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

    private static BigDecimal bd(Double v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public void create(CarCreateDto dto, MultipartFile image) {

        Car car = new Car();
        car.setTitle(dto.getTitle());
        car.setBrand(dto.getBrand());
        car.setPricePerDay(dto.getPricePerDay());

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

        // ✅ Pricing auto create (daily = car.pricePerDay)
        CarPricing cp = new CarPricing();
        cp.setCar(car);
        cp.setDailyRate(bd(car.getPricePerDay()));
        cp.setHourlyRate(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        cp.setMonthlyLeasingRate(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        cp.setFuelSurchargePerHour(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        cp.setIsActive(true);

        carPricingRepository.save(cp);
    }

    @Override
    @Transactional
    public void update(Long id, CarUpdateDto dto, MultipartFile image) {

        Car car = getById(id);

        Double oldPrice = car.getPricePerDay();

        car.setTitle(dto.getTitle());
        car.setBrand(dto.getBrand());
        car.setPricePerDay(dto.getPricePerDay());

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

        // ✅ daily sync (car.pricePerDay -> pricing.dailyRate)
        boolean priceChanged =
                (oldPrice == null && car.getPricePerDay() != null)
                        || (oldPrice != null && !oldPrice.equals(car.getPricePerDay()));

        if (priceChanged) {
            CarPricing cp = carPricingRepository.findByCar_Id(car.getId())
                    .orElseGet(() -> {
                        CarPricing x = new CarPricing();
                        x.setCar(car);
                        x.setHourlyRate(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                        x.setMonthlyLeasingRate(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                        x.setFuelSurchargePerHour(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                        x.setIsActive(true);
                        return x;
                    });

            cp.setDailyRate(bd(car.getPricePerDay()));
            carPricingRepository.save(cp);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Car car = getById(id);
        car.setIsActive(false);
        carRepository.save(car);

        // ✅ pricing də soft deactivate olsun (istəsən)
        carPricingRepository.findByCar_Id(car.getId()).ifPresent(cp -> {
            cp.setIsActive(false);
            carPricingRepository.save(cp);
        });
    }
}
