package com.example.demo.controller.web;

import com.example.demo.dto.enums.BannerType;
import com.example.demo.model.About;
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

    // ✅ Yeni Azərbaycan slug
    @GetMapping("/haqqimizda")
    public String aboutAz(Model model) {

        About about = aboutService.getActiveAbout();
        if (about == null) {
            about = new About();
        }

        model.addAttribute("banner", bannerService.getBanner(BannerType.ABOUT));
        model.addAttribute("about", about);

        return "about";
    }

    // ✅ Köhnə slug -> yeni sluga redirect (SEO üçün yaxşıdır)
    @GetMapping("/about")
    public String aboutOldRedirect() {
        return "redirect:/haqqimizda";
        // əgər 301 istəyirsənsə aşağıdakı variantı deyəcəm (controllerdən asılı olur)
    }
}