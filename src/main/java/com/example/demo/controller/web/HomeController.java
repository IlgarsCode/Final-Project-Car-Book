package com.example.demo.controller.web;

import com.example.demo.dto.enums.BannerType;
import com.example.demo.model.About;
import com.example.demo.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BannerService bannerService;
    private final AboutService aboutService;
    private final ServicePageService servicePageService;
    private final TestimonialService testimonialService;
    private final BlogService blogService;

    private final HomeStatsService homeStatsService; // ✅ əlavə et

    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute("banner", bannerService.getBanner(BannerType.HOME));

        About about = aboutService.getActiveAbout();
        if (about == null) about = new About();
        model.addAttribute("about", about);

        model.addAttribute("services", servicePageService.getActiveServices());
        model.addAttribute("testimonials", testimonialService.getActiveTestimonials());
        model.addAttribute("blogs", blogService.getActiveBlogs());

        model.addAttribute("stats", homeStatsService.getHomeStats()); // ✅ əlavə et

        return "index";
    }
}
