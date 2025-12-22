package com.example.demo.controller.web;

import com.example.demo.model.About;
import com.example.demo.services.AboutService;
import com.example.demo.services.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BannerService bannerService;
    private final AboutService aboutService;

    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute("banner", bannerService.getHomeBanner());

        About about = aboutService.getActiveAbout();
        if (about == null) {
            about = new About();
        }
        model.addAttribute("about", about);

        return "index";
    }

    @GetMapping("/services")
    public String services() {
        return "services";
    }

    @GetMapping("/car")
    public String car() {
        return "car";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/pricing")
    public String pricing() {
        return "pricing";
    }

    @GetMapping("/blog")
    public String blog() {
        return "blog";
    }

    @GetMapping("/car-single.html")
    public String carSingle() {
        return "car-single";
    }

    @GetMapping("/blog-single.html")
    public String blogSingle() {
        return "blog-single";
    }
}
