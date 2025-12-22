package com.example.demo.controller.web;

import com.example.demo.model.About;
import com.example.demo.model.Banner;
import com.example.demo.services.AboutService;
import com.example.demo.services.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AboutController {

    private final AboutService aboutService;
    private final BannerService bannerService;

    @GetMapping("/about")
    public String about(Model model) {

        About about = aboutService.getActiveAbout();
        if (about == null) {
            about = new About();
        }

        Banner banner = bannerService.getAboutBanner();

        model.addAttribute("about", about);
        model.addAttribute("banner", banner);

        return "about";
    }
}
