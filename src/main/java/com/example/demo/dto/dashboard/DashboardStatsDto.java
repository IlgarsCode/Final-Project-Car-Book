package com.example.demo.dto.dashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardStatsDto {

    private BigDecimal todayRevenue;
    private BigDecimal monthRevenue;

    private long pendingOrders;
    private long approvalWaiting;
    private long paymentFailedOrders;

    private List<String> last7DaysLabels;
    private List<BigDecimal> last7DaysRevenue;

    private List<String> statusLabels;
    private List<Long> statusCounts;

    private long latestOrdersCount;
    private long latestPaymentsCount;
}