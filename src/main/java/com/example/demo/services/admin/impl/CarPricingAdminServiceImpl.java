package com.example.demo.services.admin.impl;

import com.example.demo.dto.pricing.CarPricingCreateDto;
import com.example.demo.dto.pricing.CarPricingUpdateDto;
import com.example.demo.model.CarPricing;
import com.example.demo.repository.CarPricingRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.services.admin.CarPricingAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarPricingAdminServiceImpl implements CarPricingAdminService {

    private final CarPricingRepository carPricingRepository;
    private final CarRepository carRepository;

    @Override
    public List<CarPricing> getAllActiveRows() {
        return List.of();
    }

    @Override
    public CarPricing getByCarId(Long carId) {
        return null;
    }

    @Override
    public void createForCar(Long carId, BigDecimal hourly, BigDecimal daily, BigDecimal leasing, BigDecimal fuelSurcharge) {

    }

    @Override
    public void updateForCar(Long carId, BigDecimal hourly, BigDecimal daily, BigDecimal leasing, BigDecimal fuelSurcharge) {

    }

    @Override
    public void createOrUpdate(CarPricingCreateDto dto) {

        var car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        // car_id unique olduğu üçün: varsa update, yoxsa create
        CarPricing cp = carPricingRepository.findActiveByCarId(dto.getCarId()).orElse(null);
        if (cp == null) {
            cp = new CarPricing();
            cp.setCar(car);
        }

        cp.setHourlyRate(dto.getHourlyRate());
        cp.setDailyRate(dto.getDailyRate());
        cp.setMonthlyLeasingRate(dto.getMonthlyLeasingRate());
        cp.setFuelSurchargePerHour(dto.getFuelSurchargePerHour());
        cp.setIsActive(true);

        carPricingRepository.save(cp);
    }

    @Override
    public void update(CarPricingUpdateDto dto) {
        createOrUpdate(toCreateDto(dto)); // eyni məntiq
    }

    private CarPricingCreateDto toCreateDto(CarPricingUpdateDto dto) {
        CarPricingCreateDto c = new CarPricingCreateDto();
        c.setCarId(dto.getCarId());
        c.setHourlyRate(dto.getHourlyRate());
        c.setDailyRate(dto.getDailyRate());
        c.setMonthlyLeasingRate(dto.getMonthlyLeasingRate());
        c.setFuelSurchargePerHour(dto.getFuelSurchargePerHour());
        return c;
    }

    @Override
    public void deactivate(Long carId) {
        var cp = carPricingRepository.findActiveByCarId(carId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pricing tapılmadı"));
        cp.setIsActive(false);
        carPricingRepository.save(cp);
    }
}
