package com.example.demo.services.admin.impl;

import com.example.demo.dto.dashboard.DashboardStatsDto;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.Payment;
import com.example.demo.model.PaymentStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.services.admin.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public DashboardStatsDto getStats() {

        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);

        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDateTime monthStart = firstDay.atStartOfDay();
        LocalDateTime monthEnd = today.atTime(LocalTime.MAX);

        BigDecimal todayRevenue = paymentRepository
                .sumAmountByStatusBetween(PaymentStatus.SUCCEEDED, todayStart, todayEnd);

        BigDecimal monthRevenue = paymentRepository
                .sumAmountByStatusBetween(PaymentStatus.SUCCEEDED, monthStart, monthEnd);

        long pending = orderRepository.countByStatus(OrderStatus.PENDING);
        long approvalWaiting = orderRepository.countByStatus(OrderStatus.PAID);
        long failed = orderRepository.countByStatus(OrderStatus.PAYMENT_FAILED);

        // ---- last 7 days revenue chart
        LocalDate startDay = today.minusDays(6); // today daxil 7 gün
        LocalDateTime from = startDay.atStartOfDay();
        LocalDateTime to = today.atTime(LocalTime.MAX);

        // map day -> sum
        Map<LocalDate, BigDecimal> sumMap = new HashMap<>();
        for (var v : paymentRepository.sumSucceededByDayBetween(from, to)) {
            LocalDate d = v.getDay().toLocalDate();
            sumMap.put(d, v.getTotal() == null ? BigDecimal.ZERO : v.getTotal());
        }

        List<String> labels = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = startDay.plusDays(i);
            labels.add(d.toString()); // 2026-02-20 kimi
            values.add(sumMap.getOrDefault(d, BigDecimal.ZERO));
        }

        // ---- status pie
        var statusViews = orderRepository.countByStatusGroup();
        List<String> statusLabels = new ArrayList<>();
        List<Long> statusCounts = new ArrayList<>();
        for (var x : statusViews) {
            statusLabels.add(x.getStatus().name());
            statusCounts.add(x.getCnt());
        }

        DashboardStatsDto dto = new DashboardStatsDto();
        dto.setTodayRevenue(todayRevenue);
        dto.setMonthRevenue(monthRevenue);
        dto.setPendingOrders(pending);
        dto.setApprovalWaiting(approvalWaiting);
        dto.setPaymentFailedOrders(failed);

        dto.setLast7DaysLabels(labels);
        dto.setLast7DaysRevenue(values);

        dto.setStatusLabels(statusLabels);
        dto.setStatusCounts(statusCounts);

        return dto;
    }

    @Override
    public List<Order> latestOrders(int n) {
        return orderRepository.findLatest(PageRequest.of(0, Math.max(1, n)));
    }

    @Override
    public List<Payment> latestPaidPayments(int n) {
        return paymentRepository.findLatestSucceeded(PageRequest.of(0, Math.max(1, n)));
    }
}