package com.example.demo.controller.web;

import com.example.demo.dto.blog.BlogCommentCreateDto;
import com.example.demo.dto.blog.BlogReplyCreateDto;
import com.example.demo.dto.enums.BannerType;
import com.example.demo.model.BlogComment;
import com.example.demo.repository.BlogCommentRepository;
import com.example.demo.repository.BlogRepository;
import com.example.demo.repository.CarCategoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.BannerService;
import com.example.demo.services.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final CarCategoryRepository carCategoryRepository;

    // ✅ reply üçün login user-in fullName/email-ni DB-dən götürəcəyik
    private final UserRepository userRepository;

    @GetMapping("/blog")
    public String blogPage(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "search", required = false) String search,
            Model model
    ) {
        int size = 5;
        int pageIndex = Math.max(page - 1, 0);

        var blogsPage = blogService.getActiveBlogs(pageIndex, size, search);

        model.addAttribute("blogs", blogsPage.getContent());
        model.addAttribute("currentPage", blogsPage.getNumber() + 1);
        model.addAttribute("totalPages", blogsPage.getTotalPages());
        model.addAttribute("search", search);

        model.addAttribute("banner", bannerService.getBanner(BannerType.BLOGS));
        return "blog";
    }

    @GetMapping("/blog/{id}")
    public String blogSingle(@PathVariable Long id, Model model) {
        fillBlogSingleModel(id, model, new BlogCommentCreateDto(), new BlogReplyCreateDto());
        return "blog-single";
    }

    // ✅ Top-level comment (anonymous allowed)
    @PostMapping("/blog/{id}/comment")
    public String addComment(
            @PathVariable Long id,
            @Valid @ModelAttribute("commentForm") BlogCommentCreateDto form,
            BindingResult br,
            Model model
    ) {
        if (br.hasErrors()) {
            fillBlogSingleModel(id, model, form, new BlogReplyCreateDto());
            return "blog-single";
        }

        var blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog tapılmadı"));

        if (Boolean.FALSE.equals(blog.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog aktiv deyil");
        }

        BlogComment comment = new BlogComment();
        comment.setBlog(blog);
        comment.setParent(null); // ✅ burası comment-dir, reply deyil
        comment.setFullName(form.getFullName().trim());
        comment.setEmail(form.getEmail().trim());
        comment.setMessage(form.getMessage().trim());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setIsActive(true);

        blogCommentRepository.save(comment);

        return "redirect:/blog/" + id + "#comments";
    }

    // ✅ Reply (login required) - yalnız message gəlir
    @PostMapping("/blog/{id}/reply")
    public String addReply(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user,
            @Valid @ModelAttribute("replyForm") BlogReplyCreateDto form,
            BindingResult br,
            Model model
    ) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Reply üçün giriş etməlisən");
        }

        if (br.hasErrors()) {
            fillBlogSingleModel(id, model, new BlogCommentCreateDto(), form);
            return "blog-single";
        }

        var blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog tapılmadı"));

        if (Boolean.FALSE.equals(blog.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog aktiv deyil");
        }

        // ✅ parent comment mütləq həmin blog-a aid olmalıdır
        var parent = blogCommentRepository.findByIdAndBlog_IdAndIsActiveTrue(form.getParentId(), id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reply üçün parent comment tapılmadı"));

        // ✅ login user info
        var u = userRepository.findByEmailIgnoreCase(user.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));

        String fullName = (u.getFullName() != null && !u.getFullName().isBlank())
                ? u.getFullName().trim()
                : u.getEmail();

        BlogComment reply = new BlogComment();
        reply.setBlog(blog);
        reply.setParent(parent);
        reply.setFullName(fullName);
        reply.setEmail(u.getEmail());
        reply.setMessage(form.getMessage().trim());
        reply.setCreatedAt(LocalDateTime.now());
        reply.setIsActive(true);

        blogCommentRepository.save(reply);

        return "redirect:/blog/" + id + "#comments";
    }

    private void fillBlogSingleModel(Long id, Model model, BlogCommentCreateDto commentForm, BlogReplyCreateDto replyForm) {
        model.addAttribute("blog", blogService.getBlogDetail(id));
        model.addAttribute("banner", bannerService.getBanner(BannerType.BLOG_SINGLE));
        model.addAttribute("carCategories", carCategoryRepository.findAllWithActiveCarCount());
        model.addAttribute("recentBlogs", blogService.getRecentBlogs(id, 3));
        model.addAttribute("commentForm", commentForm);
        model.addAttribute("replyForm", replyForm);
    }
}
