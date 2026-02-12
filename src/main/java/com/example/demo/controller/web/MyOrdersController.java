package com.example.demo.controller.web;

import com.example.demo.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/my-orders")
public class MyOrdersController {

    private final OrderService orderService;

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails user, Model model) {
        var orders = orderService.getMyOrders(user.getUsername());
        model.addAttribute("orders", orders);
        return "my-orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails user,
                         Model model) {
        var order = orderService.getMyOrderDetail(user.getUsername(), id);
        model.addAttribute("order", order);
        return "my-orders/detail";
    }
}
