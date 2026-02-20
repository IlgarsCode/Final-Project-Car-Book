package com.example.demo.controller.web;

import com.example.demo.dto.booking.BookingSearchDto;
import com.example.demo.dto.checkout.TripContext;
import com.example.demo.dto.enums.BannerType;
import com.example.demo.model.About;
import com.example.demo.services.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BannerService bannerService;
    private final AboutService aboutService;
    private final ServicePageService servicePageService;
    private final TestimonialService testimonialService;
    private final BlogService blogService;
    private final CarService carService;
    private final LocationService locationService;
    private final HomeStatsService homeStatsService;

    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute("banner", bannerService.getBanner(BannerType.HOME));

        About about = aboutService.getActiveAbout();
        if (about == null) about = new About();
        model.addAttribute("about", about);

        model.addAttribute("services", servicePageService.getActiveServices());
        model.addAttribute("testimonials", testimonialService.getActiveTestimonials());
        model.addAttribute("blogs", blogService.getActiveBlogs());
        model.addAttribute("locations", locationService.getActiveLocations());
        model.addAttribute("bookingForm", new BookingSearchDto());

        model.addAttribute("stats", homeStatsService.getHomeStats());
        model.addAttribute("relatedCars", carService.getActiveCars(null).stream().limit(8).toList());

        return "index";
    }

    // ✅ /axtaris
    @PostMapping("/axtaris")
    public String axtaris(@Valid @ModelAttribute("bookingForm") BookingSearchDto form,
                          BindingResult br,
                          Model model,
                          HttpSession session) {

        if (br.hasErrors()) {
            model.addAttribute("locations", locationService.getActiveLocations());
            return "index";
        }

        // ✅ 1) TRIP_CTX-ni session-a yaz
        TripContext ctx = (TripContext) session.getAttribute("TRIP_CTX");
        if (ctx == null) ctx = new TripContext();

        ctx.setPickupLoc(form.getPickupLocationId());
        ctx.setDropoffLoc(form.getDropoffLocationId());
        ctx.setPickupDate(form.getPickupDate());
        ctx.setDropoffDate(form.getDropoffDate());

        session.setAttribute("TRIP_CTX", ctx);

        // ✅ 2) PricingController-in gözlədiyi query adları ilə redirect et
        return "redirect:/qiymetler?pickupLoc=" + form.getPickupLocationId()
                + "&dropoffLoc=" + form.getDropoffLocationId()
                + "&pickupDate=" + form.getPickupDate()
                + "&dropoffDate=" + form.getDropoffDate();
    }
}