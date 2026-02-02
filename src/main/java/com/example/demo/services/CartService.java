package com.example.demo.services;

import com.example.demo.model.Cart;

public interface CartService {
    Cart getOrCreateCartForUser(String email);
    Cart getCartForUser(String email);

    void addToCart(String email, Long carId);
    void removeItem(String email, Long itemId);
    void clearCart(String email);
}
