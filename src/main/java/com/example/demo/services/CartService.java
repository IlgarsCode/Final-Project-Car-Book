package com.example.demo.services;

import com.example.demo.dto.enums.PricingRateType;
import com.example.demo.model.Cart;

public interface CartService {
    Cart getOrCreateCartForUser(String email);
    Cart getCartForUser(String email);

    void addToCart(String email, Long carId, PricingRateType rateType, Integer unitCount);

    void removeItem(String email, Long itemId);
    void clearCart(String email);
}
