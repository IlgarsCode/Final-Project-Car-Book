package com.example.demo.controller.web;

import com.example.demo.dto.enums.BannerType;
import com.example.demo.model.About;
import com.example.demo.services.AboutService;
import com.example.demo.services.BannerService;
import com.example.demo.services.ServicePageService;
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

    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute(
                "banner",
                bannerService.getBanner(BannerType.HOME)
        );

        About about = aboutService.getActiveAbout();
        if (about == null) {
            about = new About();
        }
        model.addAttribute("about", about);

        model.addAttribute(
                "services",
                servicePageService.getActiveServices()
        );

        return "index";
    }

    @GetMapping("/car")
    public String car() {
        return "car";
    }

    @GetMapping("/pricing")
    public String pricing() {
        return "pricing";
    }

    @GetMapping("/blog")
    public String blog() {
        return "blog";
    }

    @GetMapping("/car-single")
    public String carSingle() {
        return "car-single";
    }

    @GetMapping("/blog-single")
    public String blogSingle() {
        return "blog-single";
    }
}
