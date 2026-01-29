package com.example.demo.controller.web;

import com.example.demo.dto.enums.BannerType;
import com.example.demo.services.BannerService;
import com.example.demo.services.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final BannerService bannerService;

    @GetMapping("/blog")
    public String blogPage(Model model) {

        model.addAttribute("blogs", blogService.getActiveBlogs());
        model.addAttribute(
                "banner",
                bannerService.getBanner(BannerType.BLOGS)
        );

        return "blog";
    }
}
