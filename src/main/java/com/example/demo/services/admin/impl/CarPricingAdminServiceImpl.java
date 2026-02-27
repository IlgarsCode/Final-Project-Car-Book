package com.example.demo.services.admin.impl;

import com.example.demo.dto.pricing.CarPricingCreateDto;
import com.example.demo.dto.pricing.CarPricingUpdateDto;
import com.example.demo.model.CarPricing;
import com.example.demo.repository.CarPricingRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.services.admin.CarPricingAdminService;
import jakarta.validation.Valid;
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
        return carPricingRepository.findAll();
    }

    @Override
    public CarPricing getByCarId(Long carId) {
        return carPricingRepository.findByCar_Id(carId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pricing tapılmadı"));
    }

    @Override
    public void createForCar(Long carId,
                             BigDecimal hourly, BigDecimal daily, BigDecimal leasing, BigDecimal fuelSurcharge) {
        if (carPricingRepository.existsByCar_Id(carId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu car üçün pricing artıq var");
        }

        var car = carRepository.findById(carId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        CarPricing cp = new CarPricing();
        cp.setCar(car);
        cp.setHourlyRate(nz(hourly));
        cp.setDailyRate(nz(daily));
        cp.setMonthlyLeasingRate(nz(leasing));
        cp.setFuelSurchargePerHour(fuelSurcharge);
        cp.setIsActive(true);
        cp.setHourlyDiscountActive(false);
        cp.setHourlyDiscountPercent(null);

        cp.setDailyDiscountActive(false);
        cp.setDailyDiscountPercent(null);

        cp.setLeasingDiscountActive(false);
        cp.setLeasingDiscountPercent(null);
        carPricingRepository.save(cp);
    }

    @Override
    public void updateForCar(Long carId,
                             BigDecimal hourly, BigDecimal daily, BigDecimal leasing, BigDecimal fuelSurcharge) {
        CarPricing cp = getByCarId(carId);
        cp.setHourlyRate(nz(hourly));
        cp.setDailyRate(nz(daily));
        cp.setMonthlyLeasingRate(nz(leasing));
        cp.setFuelSurchargePerHour(fuelSurcharge);
        carPricingRepository.save(cp);
    }

    @Override
    public void createOrUpdate(@Valid CarPricingCreateDto dto) {
        var car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        CarPricing cp = carPricingRepository.findByCar_Id(dto.getCarId()).orElse(null);
        if (cp == null) {
            cp = new CarPricing();
            cp.setCar(car);
        }

        cp.setHourlyRate(nz(dto.getHourlyRate()));
        cp.setDailyRate(nz(dto.getDailyRate()));
        cp.setMonthlyLeasingRate(nz(dto.getMonthlyLeasingRate()));
        cp.setFuelSurchargePerHour(dto.getFuelSurchargePerHour());
        cp.setIsActive(true);

        cp.setHourlyDiscountActive(Boolean.TRUE.equals(dto.getHourlyDiscountActive()));
        cp.setHourlyDiscountPercent(Boolean.TRUE.equals(dto.getHourlyDiscountActive()) ? dto.getHourlyDiscountPercent() : null);

        cp.setDailyDiscountActive(Boolean.TRUE.equals(dto.getDailyDiscountActive()));
        cp.setDailyDiscountPercent(Boolean.TRUE.equals(dto.getDailyDiscountActive()) ? dto.getDailyDiscountPercent() : null);

        cp.setLeasingDiscountActive(Boolean.TRUE.equals(dto.getLeasingDiscountActive()));
        cp.setLeasingDiscountPercent(Boolean.TRUE.equals(dto.getLeasingDiscountActive()) ? dto.getLeasingDiscountPercent() : null);

        carPricingRepository.save(cp);
    }

    @Override
    public void update(CarPricingUpdateDto dto) {
        CarPricingCreateDto c = new CarPricingCreateDto();
        c.setCarId(dto.getCarId());
        c.setHourlyRate(dto.getHourlyRate());
        c.setDailyRate(dto.getDailyRate());
        c.setMonthlyLeasingRate(dto.getMonthlyLeasingRate());
        c.setFuelSurchargePerHour(dto.getFuelSurchargePerHour());

        c.setHourlyDiscountActive(dto.getHourlyDiscountActive());
        c.setHourlyDiscountPercent(dto.getHourlyDiscountPercent());

        c.setDailyDiscountActive(dto.getDailyDiscountActive());
        c.setDailyDiscountPercent(dto.getDailyDiscountPercent());

        c.setLeasingDiscountActive(dto.getLeasingDiscountActive());
        c.setLeasingDiscountPercent(dto.getLeasingDiscountPercent());

        createOrUpdate(c);
    }

    @Override
    public void deactivate(Long carId) {
        CarPricing cp = getByCarId(carId);
        cp.setIsActive(false);
        carPricingRepository.save(cp);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
