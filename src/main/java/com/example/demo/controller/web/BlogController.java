package com.example.demo.controller.web;

import com.example.demo.dto.blog.BlogCommentCreateDto;
import com.example.demo.dto.enums.BannerType;
import com.example.demo.model.BlogComment;
import com.example.demo.repository.BlogCommentRepository;
import com.example.demo.repository.BlogRepository;
import com.example.demo.repository.CarCategoryRepository;
import com.example.demo.services.BannerService;
import com.example.demo.services.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final BannerService bannerService;

    private final BlogRepository blogRepository;
    private final BlogCommentRepository blogCommentRepository;

    // ✅ Car categories sidebar üçün
    private final CarCategoryRepository carCategoryRepository;

    @GetMapping("/blog")
    public String blogPage(@RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        int size = 5;
        int pageIndex = Math.max(page - 1, 0);

        var blogsPage = blogService.getActiveBlogs(pageIndex, size);

        model.addAttribute("blogs", blogsPage.getContent());
        model.addAttribute("currentPage", blogsPage.getNumber() + 1);
        model.addAttribute("totalPages", blogsPage.getTotalPages());

        model.addAttribute("banner", bannerService.getBanner(BannerType.BLOGS));
        return "blog";
    }

    @GetMapping("/blog/{id}")
    public String blogSingle(@PathVariable Long id, Model model) {

        model.addAttribute("blog", blogService.getBlogDetail(id));
        model.addAttribute("banner", bannerService.getBanner(BannerType.BLOG_SINGLE));

        // ✅ Car Categories sidebar
        model.addAttribute("carCategories", carCategoryRepository.findAllWithActiveCarCount());

        // ✅ comment form
        model.addAttribute("commentForm", new BlogCommentCreateDto());

        return "blog-single";
    }

    @PostMapping("/blog/{id}/comment")
    public String addComment(
            @PathVariable Long id,
            @Valid @ModelAttribute("commentForm") BlogCommentCreateDto form,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("blog", blogService.getBlogDetail(id));
            model.addAttribute("banner", bannerService.getBanner(BannerType.BLOG_SINGLE));

            // ✅ error olanda da sidebar gəlsin
            model.addAttribute("carCategories", carCategoryRepository.findAllWithActiveCarCount());

            return "blog-single";
        }

        var blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog tapılmadı"));

        if (Boolean.FALSE.equals(blog.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog aktiv deyil");
        }

        BlogComment comment = new BlogComment();
        comment.setBlog(blog);
        comment.setFullName(form.getFullName());
        comment.setEmail(form.getEmail());
        comment.setWebsite(form.getWebsite());
        comment.setMessage(form.getMessage());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setIsActive(true);

        blogCommentRepository.save(comment);

        return "redirect:/blog/" + id + "#comments";
    }
}
