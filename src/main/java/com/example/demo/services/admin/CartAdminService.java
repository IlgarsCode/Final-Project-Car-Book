package com.example.demo.services.admin;

import com.example.demo.dto.cart.CartAdminFilterDto;
import com.example.demo.dto.cart.CartDashboardDto;
import com.example.demo.dto.cart.CartDetailDashboardDto;
import org.springframework.data.domain.Page;

public interface CartAdminService {
    Page<CartDashboardDto> getPage(CartAdminFilterDto filter, int page, int size);
    CartDetailDashboardDto getDetail(Long cartId);

    void removeItem(Long cartId, Long itemId);
    void clearCart(Long cartId);
}
