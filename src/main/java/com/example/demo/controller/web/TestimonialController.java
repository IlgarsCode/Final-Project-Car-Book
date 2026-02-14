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

    @GetMapping("/testimonial")
    public String testimonialPage(Model model) {
        model.addAttribute("banner", bannerService.getBanner(BannerType.TESTIMONIAL));
        model.addAttribute("testimonials", testimonialService.getActiveTestimonials());
        return "testimonial";
    }

    @GetMapping("/testimonial/new")
    public String newTestimonialPage(Model model) {
        model.addAttribute("form", new TestimonialCreateDto());
        return "testimonial-create";
    }

    @PostMapping("/testimonial/new")
    public String createTestimonial(
            @Valid @ModelAttribute("form") TestimonialCreateDto form,
            BindingResult br,
            Authentication auth
    ) {
        if (br.hasErrors()) return "testimonial-create";

        testimonialService.create(auth.getName(), form);
        return "redirect:/testimonial";
    }
}
