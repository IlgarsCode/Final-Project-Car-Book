package com.example.demo.dto.order;

import com.example.demo.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrderAdminFilterDto {
    private OrderStatus status;
    private LocalDate from;
    private LocalDate to;
    private String q;
}
