package com.example.demo.dto.dashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardStatsDto {

    // KPI
    private BigDecimal todayRevenue;
    private BigDecimal monthRevenue;

    private long pendingOrders;        // PENDING
    private long approvalWaiting;      // PAID (admin təsdiqi gözləyir)
    private long paymentFailedOrders;  // PAYMENT_FAILED

    // Charts
    private List<String> last7DaysLabels;
    private List<BigDecimal> last7DaysRevenue;

    private List<String> statusLabels;
    private List<Long> statusCounts;

    // Latest tables
    private long latestOrdersCount;
    private long latestPaymentsCount;
}