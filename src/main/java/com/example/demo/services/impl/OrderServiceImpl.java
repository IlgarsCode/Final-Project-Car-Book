package com.example.demo.services.impl;

import com.example.demo.dto.checkout.CheckoutCreateDto;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.OrderStatus;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

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

        long daysBetween = ChronoUnit.DAYS.between(dto.getPickupDate(), dto.getDropoffDate());
        int rentalDays = (int) Math.max(1, daysBetween); // eyni gün -> 1 gün

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        order.setPickupLocation(dto.getPickupLocation());
        order.setDropoffLocation(dto.getDropoffLocation());
        order.setPickupDate(dto.getPickupDate());
        order.setDropoffDate(dto.getDropoffDate());
        order.setPickupTime(dto.getPickupTime());
        order.setRentalDays(rentalDays);

        BigDecimal total = BigDecimal.ZERO;

        // CartItem-ları OrderItem-a çevir
        for (var ci : cart.getItems()) {
            var car = ci.getCar();

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setCar(car);

            oi.setCarTitleSnapshot(car.getTitle());
            oi.setCarImageUrlSnapshot(car.getImageUrl());
            oi.setDailyRateSnapshot(ci.getDailyRateSnapshot());
            oi.setQuantity(ci.getQuantity());

            BigDecimal line = ci.getDailyRateSnapshot()
                    .multiply(BigDecimal.valueOf(rentalDays))
                    .multiply(BigDecimal.valueOf(ci.getQuantity()));

            oi.setLineTotal(line);

            order.getItems().add(oi);
            total = total.add(line);
        }

        order.setTotalAmount(total);

        // order save (cascade ilə items də gedəcək)
        Order saved = orderRepository.save(order);

        // cart clear
        cart.getItems().clear();
        cartRepository.save(cart);

        return saved;
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
}
