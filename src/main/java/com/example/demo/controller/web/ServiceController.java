package com.example.demo.controller.web;

import com.example.demo.dto.enums.BannerType;
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

    @GetMapping("/xidmetler")
    public String xidmetler(Model model) {

        model.addAttribute("banner", bannerService.getBanner(BannerType.SERVICE));
        model.addAttribute("services", servicePageService.getActiveServices());

        return "services";
    }
}