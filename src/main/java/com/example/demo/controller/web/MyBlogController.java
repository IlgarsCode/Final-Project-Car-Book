package com.example.demo.controller.web;

import com.example.demo.dto.blog.BlogCreateDto;
import com.example.demo.services.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class MyBlogController {

    private final BlogService blogService;

    @GetMapping("/my-blogs")
    public String myBlogs(@AuthenticationPrincipal UserDetails user,
                          @RequestParam(name = "page", defaultValue = "1") int page,
                          Model model) {

        int size = 10;
        int pageIndex = Math.max(page - 1, 0);

        var p = blogService.getMyBlogs(user.getUsername(), pageIndex, size);

        model.addAttribute("blogs", p.getContent());
        model.addAttribute("currentPage", p.getNumber() + 1);
        model.addAttribute("totalPages", p.getTotalPages());

        return "my-blogs";
    }

    @GetMapping("/my-blogs/create")
    public String createPage(Model model) {
        model.addAttribute("form", new BlogCreateDto());
        return "my-blog-create";
    }

    @PostMapping("/my-blogs/create")
    public String doCreate(@AuthenticationPrincipal UserDetails user,
                           @Valid @ModelAttribute("form") BlogCreateDto form,
                           BindingResult br,
                           @RequestParam(name = "image", required = false) MultipartFile image,
                           Model model) {

        if (br.hasErrors()) {
            return "my-blog-create";
        }

        try {
            Long id = blogService.createBlog(user.getUsername(), form, image);
            // create oldu, amma isActive=false => “pending review” kimi listdə görünəcək
            return "redirect:/my-blogs?created=" + id;

        } catch (IllegalArgumentException ex) {
            // file format problemi
            br.reject("image.invalid", ex.getMessage());
            return "my-blog-create";
        }
    }
}
