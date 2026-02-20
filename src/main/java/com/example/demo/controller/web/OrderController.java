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
@RequestMapping("/sifarislerim")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public String myOrders(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("orders", orderService.getMyOrders(user.getUsername()));
        return "orders";
    }

    @GetMapping("/{id}")
    public String orderDetail(@AuthenticationPrincipal UserDetails user,
                              @PathVariable Long id,
                              Model model) {
        model.addAttribute("order", orderService.getMyOrderDetail(user.getUsername(), id));
        return "order-detail";
    }
}