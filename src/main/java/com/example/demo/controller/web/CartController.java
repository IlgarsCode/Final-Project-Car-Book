package com.example.demo.controller.web;

import com.example.demo.dto.checkout.TripContext;
import com.example.demo.dto.enums.PricingRateType;
import com.example.demo.repository.LocationRepository;
import com.example.demo.services.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sebet")
public class CartController {

    private final CartService cartService;
    private final LocationRepository locationRepository;

    @GetMapping
    public String cartPage(@AuthenticationPrincipal UserDetails user,
                           HttpSession session,
                           Model model) {

        var cart = cartService.getCartForUser(user.getUsername());
        model.addAttribute("cart", cart);

        TripContext ctx = (TripContext) session.getAttribute("TRIP_CTX");
        model.addAttribute("trip", ctx);

        if (ctx != null) {
            String pickupName = null;
            String dropoffName = null;

            if (ctx.getPickupLoc() != null) {
                pickupName = locationRepository.findById(ctx.getPickupLoc())
                        .map(l -> l.getName()).orElse(null);
            }
            if (ctx.getDropoffLoc() != null) {
                dropoffName = locationRepository.findById(ctx.getDropoffLoc())
                        .map(l -> l.getName()).orElse(null);
            }

            model.addAttribute("pickupName", pickupName);
            model.addAttribute("dropoffName", dropoffName);
        }

        return "cart";
    }

    @PostMapping("/elave-et")
    public String addToCart(@AuthenticationPrincipal UserDetails user,
                            @RequestParam Long carId,
                            @RequestParam(name = "rateType", defaultValue = "DAILY") PricingRateType rateType,
                            @RequestParam(name = "unitCount", defaultValue = "1") Integer unitCount,

                            @RequestParam(name = "pickupLoc", required = false) Long pickupLoc,
                            @RequestParam(name = "dropoffLoc", required = false) Long dropoffLoc,
                            @RequestParam(name = "pickupDate", required = false) LocalDate pickupDate,
                            @RequestParam(name = "dropoffDate", required = false) LocalDate dropoffDate,

                            HttpSession session,
                            @RequestHeader(value = "Referer", required = false) String referer) {

        cartService.addToCart(user.getUsername(), carId, rateType, unitCount);

        TripContext ctx = (TripContext) session.getAttribute("TRIP_CTX");
        if (ctx == null) ctx = new TripContext();

        if (pickupLoc != null) ctx.setPickupLoc(pickupLoc);
        if (dropoffLoc != null) ctx.setDropoffLoc(dropoffLoc);
        if (pickupDate != null) ctx.setPickupDate(pickupDate);
        if (dropoffDate != null) ctx.setDropoffDate(dropoffDate);

        session.setAttribute("TRIP_CTX", ctx);

        return "redirect:" + (referer != null ? referer : "/sebet");
    }

    @PostMapping("/sil/{itemId}")
    public String removeItem(@AuthenticationPrincipal UserDetails user,
                             @PathVariable Long itemId) {
        cartService.removeItem(user.getUsername(), itemId);
        return "redirect:/sebet";
    }

    @PostMapping("/temizle")
    public String clear(@AuthenticationPrincipal UserDetails user,
                        HttpSession session) {
        cartService.clearCart(user.getUsername());
        session.removeAttribute("TRIP_CTX");
        return "redirect:/sebet";
    }
}