package com.example.demo.services.impl;

import com.example.demo.dto.enums.PricingRateType;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.repository.CarPricingRepository;
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
    private final CarPricingRepository carPricingRepository;

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
        return getOrCreateCartForUser(email);
    }

    @Override
    public void addToCart(String email, Long carId, PricingRateType rateType, Integer unitCount) {
        if (unitCount == null || unitCount < 1) unitCount = 1;

        var cart = getOrCreateCartForUser(email);

        var car = carRepository.findById(carId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        if (Boolean.FALSE.equals(car.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Car aktiv deyil");
        }

        // ✅ pricing mütləq pricing cədvəlindən gəlməlidir
        var pricing = carPricingRepository.findActiveByCarId(carId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu car üçün pricing tapılmadı"));

        BigDecimal unitPrice = switch (rateType) {
            case HOURLY -> nz(pricing.getHourlyRate());
            case LEASING -> nz(pricing.getMonthlyLeasingRate());
            default -> nz(pricing.getDailyRate());
        };

        BigDecimal surcharge = nz(pricing.getFuelSurchargePerHour()); // yalnız hourly üçün real təsiri var

        var existing = cartItemRepository.findByCart_IdAndCar_IdAndRateType(cart.getId(), carId, rateType);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + 1);

            // istəsən unitCount-u da artıra bilərsən, amma bu məntiqli deyil.
            // Mən saxlayıram: unitCount dəyişmir.
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setCar(car);
            item.setRateType(rateType);

            item.setUnitPriceSnapshot(unitPrice);
            item.setFuelSurchargePerHourSnapshot(surcharge);

            item.setUnitCount(unitCount);
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

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
