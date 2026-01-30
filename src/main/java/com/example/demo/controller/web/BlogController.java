package com.example.demo.controller.web;

import com.example.demo.dto.enums.BannerType;
import com.example.demo.services.BannerService;
import com.example.demo.services.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final BannerService bannerService;

    @GetMapping("/blog")
    public String blogPage(
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model
    ) {
        int size = 5;

        int pageIndex = Math.max(page - 1, 0); // 1-based -> 0-based

        var blogsPage = blogService.getActiveBlogs(pageIndex, size);

        model.addAttribute("blogs", blogsPage.getContent());
        model.addAttribute("currentPage", blogsPage.getNumber() + 1); // artıq 1-based
        model.addAttribute("totalPages", blogsPage.getTotalPages());

        model.addAttribute("banner", bannerService.getBanner(BannerType.BLOGS));

        return "blog";
    }
}