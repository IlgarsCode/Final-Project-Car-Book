package com.example.demo.controller.web;

import com.example.demo.dto.enums.BannerType;
import com.example.demo.repository.CarCategoryRepository;
import com.example.demo.services.BannerService;
import com.example.demo.services.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;
    private final BannerService bannerService;
    private final CarCategoryRepository carCategoryRepository;

    @GetMapping("/pricing")
    public String pricingPage(
            @RequestParam(name = "category", required = false) String categorySlug,

            // ✅ booking context (home formdan gələn)
            @RequestParam(name = "pickupLoc", required = false) Long pickupLoc,
            @RequestParam(name = "dropoffLoc", required = false) Long dropoffLoc,
            @RequestParam(name = "pickupDate", required = false) String pickupDate,
            @RequestParam(name = "dropoffDate", required = false) String dropoffDate,

            Model model
    ) {
        model.addAttribute("banner", bannerService.getBanner(BannerType.PRICING));

        model.addAttribute("rows", pricingService.getPricingRows(categorySlug));
        model.addAttribute("carCategories", carCategoryRepository.findAllWithActiveCarCount());
        model.addAttribute("selectedCategory", categorySlug);

        // ✅ view-ə ötür (pricing.html-də istifadə edəcəyik)
        model.addAttribute("pickupLoc", pickupLoc);
        model.addAttribute("dropoffLoc", dropoffLoc);
        model.addAttribute("pickupDate", pickupDate);
        model.addAttribute("dropoffDate", dropoffDate);

        return "pricing";
    }
}
