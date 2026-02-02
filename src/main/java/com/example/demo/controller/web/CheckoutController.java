package com.example.demo.controller.web;

import com.example.demo.dto.checkout.CheckoutCreateDto;
import com.example.demo.services.CartService;
import com.example.demo.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;

    @GetMapping("/checkout")
    public String checkoutPage(@AuthenticationPrincipal UserDetails user, Model model) {
        var cart = cartService.getCartForUser(user.getUsername());
        model.addAttribute("cart", cart);
        model.addAttribute("form", new CheckoutCreateDto());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String doCheckout(@AuthenticationPrincipal UserDetails user,
                             @Valid @ModelAttribute("form") CheckoutCreateDto form,
                             BindingResult br,
                             Model model) {

        var cart = cartService.getCartForUser(user.getUsername());

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            br.reject("cart.empty", "Cart boşdur");
        }

        if (br.hasErrors()) {
            model.addAttribute("cart", cart);
            return "checkout";
        }

        var order = orderService.checkout(user.getUsername(), form);

        return "redirect:/order/" + order.getId();
    }
}
