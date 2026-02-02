package com.example.demo.services.impl;

import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Override
    public Cart getOrCreateCartForUser(String email) {
        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));

        return cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepository.save(c);
                });
    }

    @Override
    public Cart getCartForUser(String email) {
        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));

        return cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepository.save(c);
                });
    }

    @Override
    public void addToCart(String email, Long carId) {
        var cart = getOrCreateCartForUser(email);

        var car = carRepository.findById(carId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        if (Boolean.FALSE.equals(car.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Car aktiv deyil");
        }

        var existing = cartItemRepository.findByCart_IdAndCar_Id(cart.getId(), carId);

        if (existing.isPresent()) {
            // istəsən quantity artır
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setCar(car);

            // snapshot
            double p = (car.getPricePerDay() == null) ? 0.0 : car.getPricePerDay();
            item.setDailyRateSnapshot(BigDecimal.valueOf(p));

            item.setQuantity(1);
            cartItemRepository.save(item);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public void removeItem(String email, Long itemId) {
        var cart = getCartForUser(email);

        var item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item tapılmadı"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu item sənə aid deyil");
        }

        cartItemRepository.delete(item);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(String email) {
        var cart = getCartForUser(email);
        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
}
