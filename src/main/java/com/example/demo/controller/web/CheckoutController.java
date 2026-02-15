package com.example.demo.controller.web;

import com.example.demo.dto.checkout.CheckoutCreateDto;
import com.example.demo.dto.checkout.TripContext;
import com.example.demo.repository.LocationRepository;
import com.example.demo.services.CartService;
import com.example.demo.services.OrderService;
import jakarta.servlet.http.HttpSession;
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
    private final LocationRepository locationRepository;

    @GetMapping("/checkout")
    public String checkoutPage(@AuthenticationPrincipal UserDetails user,
                               HttpSession session,
                               Model model) {

        var cart = cartService.getCartForUser(user.getUsername());
        model.addAttribute("cart", cart);

        var form = new CheckoutCreateDto();

        TripContext ctx = (TripContext) session.getAttribute("TRIP_CTX");
        model.addAttribute("trip", ctx);

        if (ctx != null) {

            if (ctx.getPickupLoc() != null) {
                String name = locationRepository.findById(ctx.getPickupLoc())
                        .map(l -> l.getName()).orElse(null);
                form.setPickupLocation(name);
            }

            if (ctx.getDropoffLoc() != null) {
                String name = locationRepository.findById(ctx.getDropoffLoc())
                        .map(l -> l.getName()).orElse(null);
                form.setDropoffLocation(name);
            }

            // ✅ DATE-LƏR BURDA DOLUR
            if (ctx.getPickupDate() != null) form.setPickupDate(ctx.getPickupDate());
            if (ctx.getDropoffDate() != null) form.setDropoffDate(ctx.getDropoffDate());
        }

        model.addAttribute("form", form);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String doCheckout(@AuthenticationPrincipal UserDetails user,
                             @Valid @ModelAttribute("form") CheckoutCreateDto form,
                             BindingResult br,
                             HttpSession session,
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

        // ✅ checkout bitdi -> TRIP_CTX sil
        session.removeAttribute("TRIP_CTX");

        return "redirect:/order/" + order.getId();
    }
}
