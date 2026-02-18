package com.example.demo.services.impl;

import com.example.demo.dto.checkout.CheckoutCreateDto;
import com.example.demo.dto.enums.PricingRateType;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final LocationRepository locationRepository;

    @Override
    public Order checkout(String email, CheckoutCreateDto dto) {

        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));

        var cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart boşdur"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart boşdur");
        }

        if (dto.getDropoffDate().isBefore(dto.getPickupDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drop-off date pick-up date-dən əvvəl ola bilməz");
        }

        String pickupName = locationRepository.findById(dto.getPickupLocationId())
                .map(Location::getName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pick-up location tapılmadı"));

        String dropoffName = locationRepository.findById(dto.getDropoffLocationId())
                .map(Location::getName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drop-off location tapılmadı"));

        long daysBetween = ChronoUnit.DAYS.between(dto.getPickupDate(), dto.getDropoffDate());
        int rentalDays = (int) Math.max(1, daysBetween);

        int rentalHours = calculateRentalHours(dto);
        int rentalMonths = calculateRentalMonths(dto);

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING); // payment gözlənir

        long nextUserNo = orderRepository.findMaxUserOrderNo(user.getId()) + 1;
        order.setUserOrderNo(nextUserNo);

        order.setPickupLocation(pickupName);
        order.setDropoffLocation(dropoffName);

        order.setPickupDate(dto.getPickupDate());
        order.setDropoffDate(dto.getDropoffDate());
        order.setPickupTime(dto.getPickupTime());
        order.setRentalDays(rentalDays);

        BigDecimal total = BigDecimal.ZERO;

        for (var ci : cart.getItems()) {
            var car = ci.getCar();

            PricingRateType rateType = ci.getRateType();
            BigDecimal unitPrice = nz(ci.getUnitPriceSnapshot());
            BigDecimal surcharge = nz(ci.getFuelSurchargePerHourSnapshot());
            int qty = (ci.getQuantity() == null || ci.getQuantity() < 1) ? 1 : ci.getQuantity();

            int units;
            BigDecimal line;

            if (rateType == PricingRateType.HOURLY) {
                units = rentalHours;
                line = unitPrice.add(surcharge)
                        .multiply(BigDecimal.valueOf(units))
                        .multiply(BigDecimal.valueOf(qty));
            } else if (rateType == PricingRateType.LEASING) {
                units = rentalMonths;
                line = unitPrice
                        .multiply(BigDecimal.valueOf(units))
                        .multiply(BigDecimal.valueOf(qty));
            } else {
                units = rentalDays;
                line = unitPrice
                        .multiply(BigDecimal.valueOf(units))
                        .multiply(BigDecimal.valueOf(qty));
            }

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setCar(car);

            oi.setCarTitleSnapshot(car.getTitle());
            oi.setCarImageUrlSnapshot(car.getImageUrl());

            oi.setRateType(rateType);
            oi.setUnitPriceSnapshot(unitPrice);
            oi.setFuelSurchargePerHourSnapshot(surcharge);

            oi.setBaseUnitPriceSnapshot(ci.getBaseUnitPriceSnapshot());
            oi.setDiscountPercentSnapshot(ci.getDiscountPercentSnapshot());

            oi.setUnitCountSnapshot(units);
            oi.setQuantity(qty);
            oi.setLineTotal(line);

            order.getItems().add(oi);
            total = total.add(line);
        }

        order.setTotalAmount(total);

        // ❗ Cart-ı burada silmirik (payment success olanda silinəcək)
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getMyOrders(String email) {
        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));
        return orderRepository.findAllByUser_IdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public Order getMyOrderDetail(String email, Long orderId) {
        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));
        return orderRepository.findByIdAndUser_Id(orderId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order tapılmadı"));
    }

    private int calculateRentalHours(CheckoutCreateDto dto) {
        LocalTime pickup = parseTimeOrNull(dto.getPickupTime());
        LocalTime dropoff = parseTimeOrNull(dto.getDropoffTime());
        if (pickup == null || dropoff == null) return 1;

        LocalDateTime start = LocalDateTime.of(dto.getPickupDate(), pickup);
        LocalDateTime end = LocalDateTime.of(dto.getDropoffDate(), dropoff);

        if (end.isBefore(start)) return 1;

        long minutes = ChronoUnit.MINUTES.between(start, end);
        int hours = (int) Math.ceil(minutes / 60.0);
        return Math.max(1, hours);
    }

    private int calculateRentalMonths(CheckoutCreateDto dto) {
        long days = ChronoUnit.DAYS.between(dto.getPickupDate(), dto.getDropoffDate());
        int rentalDays = (int) Math.max(1, days);
        int months = (int) Math.ceil(rentalDays / 30.0);
        return Math.max(1, months);
    }

    private LocalTime parseTimeOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalTime.parse(s.trim(), DateTimeFormatter.ofPattern("H:mm"));
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
