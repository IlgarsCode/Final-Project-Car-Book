package com.example.demo.controller.web;

import com.example.demo.dto.enums.BannerType;
import com.example.demo.services.BannerService;
import com.example.demo.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final BannerService bannerService;

    @GetMapping("/car")
    public String carPage(Model model) {

        model.addAttribute(
                "banner",
                bannerService.getBanner(BannerType.CAR)
        );

        model.addAttribute(
                "cars",
                carService.getActiveCars()
        );

        return "car";
    }
}
