package com.example.demo.controller.web;

import com.example.demo.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String cartPage(@AuthenticationPrincipal UserDetails user, Model model) {
        var cart = cartService.getCartForUser(user.getUsername());
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@AuthenticationPrincipal UserDetails user,
                            @RequestParam Long carId,
                            @RequestHeader(value = "Referer", required = false) String referer) {

        cartService.addToCart(user.getUsername(), carId);

        // haradan add etmisənsə ora qayıtsın
        return "redirect:" + (referer != null ? referer : "/cart");
    }

    @PostMapping("/remove/{itemId}")
    public String removeItem(@AuthenticationPrincipal UserDetails user,
                             @PathVariable Long itemId) {
        cartService.removeItem(user.getUsername(), itemId);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clear(@AuthenticationPrincipal UserDetails user) {
        cartService.clearCart(user.getUsername());
        return "redirect:/cart";
    }
}
