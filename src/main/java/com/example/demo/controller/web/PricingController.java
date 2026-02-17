package com.example.demo.controller.web;

import com.example.demo.dto.checkout.TripContext;
import com.example.demo.dto.enums.BannerType;
import com.example.demo.repository.CarCategoryRepository;
import com.example.demo.repository.CarSegmentRepository;
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
    private final CarSegmentRepository carSegmentRepository;

    @GetMapping("/pricing")
    public String pricingPage(
            @RequestParam(name = "category", required = false) String categorySlug,
            @RequestParam(name = "pickupLoc", required = false) Long pickupLoc,
            @RequestParam(name = "dropoffLoc", required = false) Long dropoffLoc,
            @RequestParam(name = "pickupDate", required = false) LocalDate pickupDate,
            @RequestParam(name = "dropoffDate", required = false) LocalDate dropoffDate,
            @RequestParam(name = "segment", required = false) String segmentSlug,
            HttpSession session,
            Model model
    ) {
        model.addAttribute("banner", bannerService.getBanner(BannerType.PRICING));

        // ✅ tarixlərlə çağır
        if (segmentSlug != null && !segmentSlug.isBlank()) {
            model.addAttribute("rows", pricingService.getPricingRows(categorySlug, segmentSlug, pickupDate, dropoffDate));
        } else {
            model.addAttribute("rows", pricingService.getPricingRows(categorySlug, pickupDate, dropoffDate));
        }

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
        model.addAttribute("segments", carSegmentRepository.findAllWithActiveCarCount());
        model.addAttribute("selectedSegment", segmentSlug);

        return "pricing";
    }
}
