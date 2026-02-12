package com.example.demo.services.admin.impl;

import com.example.demo.dto.cart.*;
import com.example.demo.model.Cart;
import com.example.demo.model.User;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.services.admin.CartAdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartAdminServiceImpl implements CartAdminService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Page<CartDashboardDto> getPage(CartAdminFilterDto filter, int page, int size) {

        final CartAdminFilterDto f = (filter == null) ? new CartAdminFilterDto() : filter;

        int p = Math.max(page, 0);
        int s = Math.min(Math.max(size, 5), 50);

        Specification<Cart> spec = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            Join<Cart, User> u = root.join("user");

            if (f.getQ() != null && !f.getQ().isBlank()) {
                String like = "%" + f.getQ().toLowerCase().trim() + "%";
                predicates.add(cb.like(cb.lower(u.get("email")), like));
            }

            query.orderBy(cb.desc(root.get("updatedAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "updatedAt"));

        return cartRepository.findAll(spec, pageable)
                .map(this::toDashboardDto);
    }

    @Override
    public CartDetailDashboardDto getDetail(Long cartId) {
        Cart c = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart tapılmadı"));

        CartDetailDashboardDto dto = new CartDetailDashboardDto();
        dto.setId(c.getId());
        dto.setUserId(c.getUser().getId());
        dto.setUserEmail(c.getUser().getEmail());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());

        dto.setItems(
                c.getItems().stream().map(ci -> {
                    CartItemDashboardDto x = new CartItemDashboardDto();
                    x.setId(ci.getId());
                    x.setCarId(ci.getCar().getId());
                    x.setCarTitle(ci.getCar().getTitle());
                    x.setCarSlug(ci.getCar().getSlug());
                    x.setRateType(ci.getRateType());
                    x.setUnitPriceSnapshot(ci.getUnitPriceSnapshot());
                    x.setFuelSurchargePerHourSnapshot(ci.getFuelSurchargePerHourSnapshot());
                    x.setUnitCount(ci.getUnitCount());
                    x.setQuantity(ci.getQuantity());
                    x.setAddedAt(ci.getAddedAt());
                    return x;
                }).toList()
        );

        return dto;
    }

    @Override
    @Transactional
    public void removeItem(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item tapılmadı");
        }
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    @Transactional
    public void clearCart(Long cartId) {
        Cart c = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart tapılmadı"));
        c.getItems().clear();          // orphanRemoval true -> siləcək
        cartRepository.save(c);
    }

    @Override
    public void removeItem(Long cartId, Long itemId) {

    }

    private CartDashboardDto toDashboardDto(Cart c) {
        CartDashboardDto dto = new CartDashboardDto();
        dto.setId(c.getId());
        dto.setUserId(c.getUser().getId());
        dto.setUserEmail(c.getUser().getEmail());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        dto.setItemCount(c.getItems() == null ? 0 : c.getItems().size());
        return dto;
    }
}
