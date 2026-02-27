package com.example.demo.dto.cart;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CartDetailDashboardDto {
    private Long id;

    private Long userId;
    private String userEmail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CartItemDashboardDto> items;
}
