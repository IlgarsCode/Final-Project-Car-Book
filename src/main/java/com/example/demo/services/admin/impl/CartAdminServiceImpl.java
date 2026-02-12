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
import java.util.*;
import java.util.stream.Collectors;

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

            // ✅ C VARIANT: yalnız içində item olan cart-ları göstər
            var sq = query.subquery(Long.class);
            var ci = sq.from(com.example.demo.model.CartItem.class);
            sq.select(cb.literal(1L));
            sq.where(cb.equal(ci.get("cart").get("id"), root.get("id")));
            predicates.add(cb.exists(sq));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Cart> cartPage = cartRepository.findAll(spec, pageable);

        // ✅ itemCount-u bulk çıxar (N+1 yox)
        List<Long> cartIds = cartPage.getContent().stream().map(Cart::getId).toList();
        Map<Long, Long> countMap = cartIds.isEmpty()
                ? Map.of()
                : cartItemRepository.countItemsByCartIds(cartIds).stream()
                .collect(Collectors.toMap(CartItemRepository.CartCountView::getCartId, CartItemRepository.CartCountView::getCnt));

        return cartPage.map(c -> toDashboardDto(c, countMap.getOrDefault(c.getId(), 0L)));
    }

    @Override
    public CartDetailDashboardDto getDetail(Long cartId) {
        Cart c = cartRepository.findWithItemsById(cartId)
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
    public void removeItem(Long cartId, Long itemId) {
        var item = cartItemRepository.findByIdAndCart_Id(itemId, cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item tapılmadı"));

        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void clearCart(Long cartId) {
        Cart c = cartRepository.findWithItemsById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart tapılmadı"));
        c.getItems().clear(); // orphanRemoval -> siləcək
        cartRepository.save(c);
    }

    private CartDashboardDto toDashboardDto(Cart c, long itemCount) {
        CartDashboardDto dto = new CartDashboardDto();
        dto.setId(c.getId());
        dto.setUserId(c.getUser().getId());
        dto.setUserEmail(c.getUser().getEmail());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        dto.setItemCount(itemCount);
        return dto;
    }
}