package com.example.demo.controller.web;

import com.example.demo.enums.BannerType;
import com.example.demo.services.BannerService;
import com.example.demo.services.ServicePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ServiceController {

    private final BannerService bannerService;
    private final ServicePageService servicePageService;

    @GetMapping("/services")
    public String services(Model model) {

        // 🔹 Banner (SERVICE → yoxdursa DEFAULT)
        model.addAttribute(
                "banner",
                bannerService.getBanner(BannerType.SERVICE)
        );

        // 🔹 Services list
        model.addAttribute(
                "services",
                servicePageService.getActiveServices()
        );

        return "services";
    }
}
