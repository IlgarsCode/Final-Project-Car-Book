package com.example.demo.dto.cart;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CartDashboardDto {
    private Long id;

    private Long userId;
    private String userEmail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private long itemCount;
}
