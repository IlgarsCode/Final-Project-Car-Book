package com.example.demo.dto.order;

import com.example.demo.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrderAdminFilterDto {
    private OrderStatus status;
    private LocalDate from;   // createdAt üçün start (date)
    private LocalDate to;     // createdAt üçün end (date)
    private String q;         // email/pickup/dropoff axtarış
}
