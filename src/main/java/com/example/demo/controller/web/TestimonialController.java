package com.example.demo.controller.web;

import com.example.demo.dto.enums.BannerType;
import com.example.demo.dto.testimonial.TestimonialCreateDto;
import com.example.demo.services.BannerService;
import com.example.demo.services.TestimonialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class TestimonialController {

    private final TestimonialService testimonialService;
    private final BannerService bannerService;

    // ✅ AZ slug: /reyler
    @GetMapping("/reyler")
    public String testimonialPage(Model model) {
        model.addAttribute("banner", bannerService.getBanner(BannerType.TESTIMONIAL));
        model.addAttribute("testimonials", testimonialService.getActiveTestimonials());
        return "testimonial";
    }

    // ✅ AZ slug: /reyler/yeni
    @GetMapping("/reyler/yeni")
    public String newTestimonialPage(Model model, Authentication auth) {
        // login yoxdursa yönləndir (yoxsa create-də partlayır)
        if (auth == null || auth.getName() == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("form", new TestimonialCreateDto());
        return "testimonial-create";
    }

    // ✅ POST: /reyler/yeni
    @PostMapping("/reyler/yeni")
    public String createTestimonial(
            @Valid @ModelAttribute("form") TestimonialCreateDto form,
            BindingResult br,
            Authentication auth,
            Model model
    ) {
        if (auth == null || auth.getName() == null) {
            return "redirect:/auth/login";
        }

        if (br.hasErrors()) {
            // səhv olanda form geri qayıtsın
            model.addAttribute("form", form);
            return "testimonial-create";
        }

        testimonialService.create(auth.getName(), form);
        return "redirect:/reyler";
    }
}