package com.example.demo.controller.web;

import com.example.demo.dto.car.CarReviewCreateDto;
import com.example.demo.dto.checkout.TripContext;
import com.example.demo.dto.enums.BannerType;
import com.example.demo.dto.enums.PricingRateType;
import com.example.demo.repository.CarCategoryRepository;
import com.example.demo.repository.CarSegmentRepository;
import com.example.demo.services.BannerService;
import com.example.demo.services.CarReviewService;
import com.example.demo.services.CarService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final CarReviewService carReviewService;
    private final BannerService bannerService;
    private final CarCategoryRepository carCategoryRepository;
    private final CarSegmentRepository carSegmentRepository;

    @GetMapping("/car")
    public String carPage(@RequestParam(name = "category", required = false) String categorySlug,
                          @RequestParam(name = "segment", required = false) String segmentSlug,
                          Model model) {
        model.addAttribute("banner", bannerService.getBanner(BannerType.CAR));
        model.addAttribute("cars", carService.getActiveCars(categorySlug, segmentSlug));

        // frontend dropdown üçün hazır saxla
        model.addAttribute("carCategories", carCategoryRepository.findAllWithActiveCarCount());
        model.addAttribute("segments", carSegmentRepository.findAllWithActiveCarCount());
        model.addAttribute("selectedCategory", categorySlug);
        model.addAttribute("selectedSegment", segmentSlug);

        return "car";
    }

    @GetMapping("/car/{slug}")
    public String carSingle(@PathVariable String slug,
                            @RequestParam(name = "rate", required = false) PricingRateType rate,

                            @RequestParam(name = "pickupLoc", required = false) Long pickupLoc,
                            @RequestParam(name = "dropoffLoc", required = false) Long dropoffLoc,
                            @RequestParam(name = "pickupDate", required = false) LocalDate pickupDate,
                            @RequestParam(name = "dropoffDate", required = false) LocalDate dropoffDate,

                            @RequestParam(name = "rpage", defaultValue = "0") int rpage,
                            HttpSession session,
                            Model model) {

        PricingRateType selected = (rate == null) ? PricingRateType.DAILY : rate;
        var car = carService.getCarDetailBySlug(slug, selected);

        model.addAttribute("banner", bannerService.getBanner(BannerType.CAR_SINGLE));
        model.addAttribute("car", car);
        model.addAttribute("relatedCars", carService.getRelatedCars(car.getId(), 3));

        TripContext ctx = (TripContext) session.getAttribute("TRIP_CTX");
        if (ctx == null) ctx = new TripContext();

        if (pickupLoc != null) ctx.setPickupLoc(pickupLoc);
        if (dropoffLoc != null) ctx.setDropoffLoc(dropoffLoc);
        if (pickupDate != null) ctx.setPickupDate(pickupDate);
        if (dropoffDate != null) ctx.setDropoffDate(dropoffDate);

        session.setAttribute("TRIP_CTX", ctx);
        model.addAttribute("trip", ctx);

        var reviewsPage = carReviewService.getActiveReviewsByCarSlug(slug, rpage, 5);
        model.addAttribute("reviewsPage", reviewsPage);
        model.addAttribute("reviews", reviewsPage.getContent());
        model.addAttribute("rpage", rpage);

        model.addAttribute("reviewCount", carReviewService.countActiveByCarSlug(slug));
        model.addAttribute("reviewStats", carReviewService.getStatsByCarSlug(slug));
        model.addAttribute("reviewForm", new CarReviewCreateDto());

        return "car-single";
    }

    @PostMapping("/car/{slug}/review")
    public String addReview(@PathVariable String slug,
                            @RequestParam(name = "rpage", defaultValue = "0") int rpage,
                            @Valid @ModelAttribute("reviewForm") CarReviewCreateDto form,
                            BindingResult bindingResult,
                            Model model) {

        var car = carService.getCarDetailBySlug(slug);

        if (bindingResult.hasErrors()) {
            model.addAttribute("banner", bannerService.getBanner(BannerType.CAR_SINGLE));
            model.addAttribute("car", car);
            model.addAttribute("relatedCars", carService.getRelatedCars(car.getId(), 3));

            var reviewsPage = carReviewService.getActiveReviewsByCarSlug(slug, rpage, 5);
            model.addAttribute("reviewsPage", reviewsPage);
            model.addAttribute("reviews", reviewsPage.getContent());
            model.addAttribute("rpage", rpage);

            model.addAttribute("reviewCount", carReviewService.countActiveByCarSlug(slug));
            model.addAttribute("reviewStats", carReviewService.getStatsByCarSlug(slug));

            return "car-single";
        }

        carReviewService.create(slug, form);
        return "redirect:/car/" + slug + "?rpage=0#pills-review";
    }
}
