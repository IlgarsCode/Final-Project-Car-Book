package com.example.demo.services.admin;

import com.example.demo.dto.dashboard.DashboardStatsDto;
import com.example.demo.model.Order;
import com.example.demo.model.Payment;

import java.util.List;

public interface DashboardService {
    DashboardStatsDto getStats();
    List<Order> latestOrders(int n);
    List<Payment> latestPaidPayments(int n);
}