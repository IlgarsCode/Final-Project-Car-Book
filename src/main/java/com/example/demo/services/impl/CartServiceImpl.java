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
import jakarta.transaction.Transactional;
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
    @Transactional
    public Cart getOrCreateCartForUser(String email) {
        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));

        return cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    c.setCreatedAt(LocalDateTime.now());
                    c.setUpdatedAt(LocalDateTime.now());
                    return cartRepository.save(c);
                });
    }

    @Override
    @Transactional
    public Cart getCartForUser(String email) {
        return getOrCreateCartForUser(email);
    }

    @Override
    @Transactional
    public void addToCart(String email, Long carId, PricingRateType rateType, Integer unitCount) {

        if (carId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "carId boş ola bilməz");
        }
        if (rateType == null) {
            rateType = PricingRateType.DAILY;
        }
        if (unitCount == null || unitCount < 1) unitCount = 1;

        var cart = getOrCreateCartForUser(email);

        var car = carRepository.findById(carId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car tapılmadı"));

        if (Boolean.FALSE.equals(car.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Car aktiv deyil");
        }

        var pricing = carPricingRepository.findActiveByCarId(carId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu car üçün pricing tapılmadı"));

        // ✅ base (endirimdən əvvəl)
        BigDecimal baseUnitPrice = switch (rateType) {
            case HOURLY -> nz(pricing.getHourlyRate());
            case LEASING -> nz(pricing.getMonthlyLeasingRate());
            default -> nz(pricing.getDailyRate());
        };

        // ✅ effective (endirim tətbiq olunmuş)
        BigDecimal effectiveUnitPrice = switch (rateType) {
            case HOURLY -> nz(pricing.getEffectiveHourlyRate());
            case LEASING -> nz(pricing.getEffectiveMonthlyLeasingRate());
            default -> nz(pricing.getEffectiveDailyRate());
        };

        // ✅ discount snapshot: rateType-ə görə seç
        Boolean discountActiveSnapshot = switch (rateType) {
            case HOURLY -> pricing.getHourlyDiscountActive();
            case LEASING -> pricing.getLeasingDiscountActive();
            default -> pricing.getDailyDiscountActive();
        };

        BigDecimal discountPercentSnapshot = switch (rateType) {
            case HOURLY -> pricing.getHourlyDiscountPercent();
            case LEASING -> pricing.getLeasingDiscountPercent();
            default -> pricing.getDailyDiscountPercent();
        };

        if (!Boolean.TRUE.equals(discountActiveSnapshot)) {
            discountPercentSnapshot = null;
        }

        // hourly üçün ayrıca surcharge var (endirimə düşmür)
        BigDecimal surcharge = nz(pricing.getFuelSurchargePerHour());

        var existingOpt = cartItemRepository.findByCart_IdAndCar_IdAndRateType(cart.getId(), carId, rateType);

        if (existingOpt.isPresent()) {
            CartItem item = existingOpt.get();

            int q = item.getQuantity() == null ? 1 : item.getQuantity();
            item.setQuantity(q + 1);

            if (item.getUnitCount() == null || item.getUnitCount() < 1) {
                item.setUnitCount(unitCount);
            }

            // ✅ snapshot-ları yenilə
            item.setBaseUnitPriceSnapshot(baseUnitPrice);
            item.setUnitPriceSnapshot(effectiveUnitPrice);
            item.setDiscountPercentSnapshot(discountPercentSnapshot);
            item.setFuelSurchargePerHourSnapshot(surcharge);

            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setCar(car);
            item.setRateType(rateType);

            item.setBaseUnitPriceSnapshot(baseUnitPrice);
            item.setUnitPriceSnapshot(effectiveUnitPrice);
            item.setDiscountPercentSnapshot(discountPercentSnapshot);

            item.setFuelSurchargePerHourSnapshot(surcharge);

            item.setUnitCount(unitCount);
            item.setQuantity(1);
            item.setAddedAt(LocalDateTime.now());

            cartItemRepository.save(item);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeItem(String email, Long itemId) {
        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));

        var cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart tapılmadı"));

        var item = cartItemRepository.findByIdAndCart_Id(itemId, cart.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item tapılmadı"));

        cartItemRepository.delete(item);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(String email) {
        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));

        var cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart tapılmadı"));

        cartItemRepository.deleteByCart_Id(cart.getId());

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
