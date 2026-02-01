package com.example.demo.controller.web;

import com.example.demo.dto.enums.BannerType;
import com.example.demo.services.BannerService;
import com.example.demo.services.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;
    private final BannerService bannerService;

    @GetMapping("/pricing")
    public String pricingPage(Model model) {

        model.addAttribute("banner", bannerService.getBanner(BannerType.PRICING));
        model.addAttribute("pricingRows", pricingService.getActivePricingRows());

        return "pricing";
    }
}