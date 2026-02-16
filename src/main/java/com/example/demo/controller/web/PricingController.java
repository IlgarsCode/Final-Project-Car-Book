package com.example.demo.controller.web;

import com.example.demo.dto.checkout.TripContext;
import com.example.demo.dto.enums.BannerType;
import com.example.demo.repository.CarCategoryRepository;
import com.example.demo.services.BannerService;
import com.example.demo.services.PricingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;
    private final BannerService bannerService;
    private final CarCategoryRepository carCategoryRepository;

    @GetMapping("/pricing")
    public String pricingPage(
            @RequestParam(name = "category", required = false) String categorySlug,

            @RequestParam(name = "pickupLoc", required = false) Long pickupLoc,
            @RequestParam(name = "dropoffLoc", required = false) Long dropoffLoc,
            @RequestParam(name = "pickupDate", required = false) LocalDate pickupDate,
            @RequestParam(name = "dropoffDate", required = false) LocalDate dropoffDate,

            HttpSession session,
            Model model
    ) {
        model.addAttribute("banner", bannerService.getBanner(BannerType.PRICING));
        model.addAttribute("carCategories", carCategoryRepository.findAllWithActiveCarCount());
        model.addAttribute("selectedCategory", categorySlug);

        TripContext ctx = (TripContext) session.getAttribute("TRIP_CTX");
        if (ctx == null) ctx = new TripContext();

        if (pickupLoc != null) ctx.setPickupLoc(pickupLoc);
        if (dropoffLoc != null) ctx.setDropoffLoc(dropoffLoc);
        if (pickupDate != null) ctx.setPickupDate(pickupDate);
        if (dropoffDate != null) ctx.setDropoffDate(dropoffDate);

        session.setAttribute("TRIP_CTX", ctx);
        model.addAttribute("trip", ctx);

        // ✅ filtr üçün tarixləri session ctx-dən götür (param null olsa belə ctx-də ola bilər)
        LocalDate p = ctx.getPickupDate();
        LocalDate d = ctx.getDropoffDate();

        model.addAttribute("rows", pricingService.getPricingRows(categorySlug, p, d));

        return "pricing";
    }
}
