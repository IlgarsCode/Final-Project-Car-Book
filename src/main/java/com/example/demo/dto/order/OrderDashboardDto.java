package com.example.demo.dto.order;

import com.example.demo.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderDashboardDto {
    private Long id;

    private OrderStatus status;

    private String userEmail;

    private String pickupLocation;
    private String dropoffLocation;

    private LocalDate pickupDate;
    private LocalDate dropoffDate;

    private Integer rentalDays;
    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    private long itemCount;
}
