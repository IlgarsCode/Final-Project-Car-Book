package com.example.demo.repository;

import com.example.demo.dto.enums.PricingRateType;
import com.example.demo.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCart_IdAndCar_IdAndRateType(Long cartId, Long carId, PricingRateType rateType);
    long countByCart_Id(Long cartId);
}
