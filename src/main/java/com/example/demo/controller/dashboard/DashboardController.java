package com.example.demo.controller.dashboard;

import com.example.demo.services.admin.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public String index(Model model) {

        var stats = dashboardService.getStats();
        var latestOrders = dashboardService.latestOrders(8);
        var latestPayments = dashboardService.latestPaidPayments(8);

        stats.setLatestOrdersCount(latestOrders.size());
        stats.setLatestPaymentsCount(latestPayments.size());

        model.addAttribute("stats", stats);
        model.addAttribute("latestOrders", latestOrders);
        model.addAttribute("latestPayments", latestPayments);

        return "dashboard/index";
    }
}