package com.example.demo.controller.web;

import com.example.demo.dto.enums.BannerType;
import com.example.demo.services.BannerService;
import com.example.demo.services.TestimonialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TestimonialController {

    private final TestimonialService testimonialService;
    private final BannerService bannerService;

    @GetMapping("/testimonial")
    public String testimonialPage(Model model) {

        model.addAttribute(
                "banner",
                bannerService.getBanner(BannerType.TESTIMONIAL)
        );

        model.addAttribute(
                "testimonials",
                testimonialService.getActiveTestimonials()
        );

        return "testimonial";
    }
}

