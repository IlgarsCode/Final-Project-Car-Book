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

        TripContext ctx = (TripContext) session.getAttribute("TRIP_CTX");
        model.addAttribute("trip", ctx);

        var checkoutForm = new CheckoutCreateDto();

        if (ctx != null) {
            if (ctx.getPickupLoc() != null) {
                String name = locationRepository.findById(ctx.getPickupLoc())
                        .map(l -> l.getName()).orElse(null);
                if (name != null) checkoutForm.setPickupLocation(name);
            }

            if (ctx.getDropoffLoc() != null) {
                String name = locationRepository.findById(ctx.getDropoffLoc())
                        .map(l -> l.getName()).orElse(null);
                if (name != null) checkoutForm.setDropoffLocation(name);
            }

            if (ctx.getPickupDate() != null) checkoutForm.setPickupDate(ctx.getPickupDate());
            if (ctx.getDropoffDate() != null) checkoutForm.setDropoffDate(ctx.getDropoffDate());
        }

        // ❗ form adı "form" YOX, "checkoutForm"
        model.addAttribute("checkoutForm", checkoutForm);

        return "checkout";
    }

    @PostMapping("/checkout")
    public String doCheckout(@AuthenticationPrincipal UserDetails user,
                             @Valid @ModelAttribute("checkoutForm") CheckoutCreateDto checkoutForm,
                             BindingResult br,
                             HttpSession session,
                             Model model) {

        var cart = cartService.getCartForUser(user.getUsername());

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            br.reject("cart.empty", "Cart boşdur");
        }

        if (br.hasErrors()) {
            model.addAttribute("cart", cart);
            model.addAttribute("trip", session.getAttribute("TRIP_CTX"));
            // checkoutForm avtomatik modeldə olacaq (ModelAttribute adı ilə)
            return "checkout";
        }

        var order = orderService.checkout(user.getUsername(), checkoutForm);

        session.removeAttribute("TRIP_CTX");
        return "redirect:/order/" + order.getId();
    }
}
