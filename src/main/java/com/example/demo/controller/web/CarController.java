package com.example.demo.controller.web;

import com.example.demo.dto.car.CarReviewCreateDto;
import com.example.demo.dto.enums.BannerType;
import com.example.demo.services.BannerService;
import com.example.demo.services.CarReviewService;
import com.example.demo.services.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final CarReviewService carReviewService;
    private final BannerService bannerService;

    @GetMapping("/car")
    public String carPage(Model model) {
        model.addAttribute("banner", bannerService.getBanner(BannerType.CAR));
        model.addAttribute("cars", carService.getActiveCars());
        return "car";
    }

    @GetMapping("/car/{slug}")
    public String carSingle(@PathVariable String slug, Model model) {

        var car = carService.getCarDetailBySlug(slug);

        model.addAttribute("banner", bannerService.getBanner(BannerType.CAR_SINGLE));
        model.addAttribute("car", car);
        model.addAttribute("relatedCars", carService.getRelatedCars(car.getId(), 3));

        // ✅ Review list + say
        model.addAttribute("reviews", carReviewService.getActiveReviewsByCarSlug(slug));
        model.addAttribute("reviewCount", carReviewService.countActiveByCarSlug(slug));

        // ✅ Form üçün boş dto
        model.addAttribute("reviewForm", new CarReviewCreateDto());

        return "car-single";
    }

    @PostMapping("/car/{slug}/review")
    public String addReview(@PathVariable String slug,
                            @Valid @ModelAttribute("reviewForm") CarReviewCreateDto form,
                            BindingResult bindingResult,
                            Model model) {

        var car = carService.getCarDetailBySlug(slug);

        if (bindingResult.hasErrors()) {

            model.addAttribute("banner", bannerService.getBanner(BannerType.CAR_SINGLE));
            model.addAttribute("car", car);
            model.addAttribute("relatedCars", carService.getRelatedCars(car.getId(), 3));

            model.addAttribute("reviews", carReviewService.getActiveReviewsByCarSlug(slug));
            model.addAttribute("reviewCount", carReviewService.countActiveByCarSlug(slug));

            return "car-single";
        }

        carReviewService.create(slug, form);

        return "redirect:/car/" + slug + "#pills-review";
    }
}
