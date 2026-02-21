package com.example.demo.services;

import com.example.demo.dto.checkout.CheckoutCreateDto;
import com.example.demo.model.Order;

import java.util.List;

public interface OrderService {

    Order checkout(String email, CheckoutCreateDto dto);

    List<Order> getMyOrders(String email);

    Order getMyOrderDetail(String email, Long userOrderNo);
}