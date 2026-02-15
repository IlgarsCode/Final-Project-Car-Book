package com.example.demo.controller.web;

import com.example.demo.dto.blog.BlogCommentCreateDto;
import com.example.demo.dto.blog.BlogCreateDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final BannerService bannerService;

    private final BlogRepository blogRepository;
    private final BlogCommentRepository blogCommentRepository;
    private final CarCategoryRepository carCategoryRepository;

    private final UserRepository userRepository;

    // =========================
    // PUBLIC LIST
    // =========================
    @GetMapping("/blog")
    public String blogPage(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "tag", required = false) String tag,
            Model model
    ) {
        int size = 9;
        int pageIndex = Math.max(page - 1, 0);

        var blogsPage = blogService.getActiveBlogs(pageIndex, size, search, tag);

        model.addAttribute("blogs", blogsPage.getContent());
        model.addAttribute("currentPage", blogsPage.getNumber() + 1);
        model.addAttribute("totalPages", blogsPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);

        model.addAttribute("banner", bannerService.getBanner(BannerType.BLOGS));
        return "blog";
    }

    // =========================
    // BLOG CREATE (USER)
    // =========================
    @GetMapping("/blog/new")
    public String newBlogPage(
            @AuthenticationPrincipal UserDetails user,
            Model model
    ) {
        if (user == null) {
            // normalda security redirect edəcək, bu sadəcə safety
            return "redirect:/auth/login";
        }

        model.addAttribute("banner", bannerService.getBanner(BannerType.BLOGS));
        model.addAttribute("form", new BlogCreateDto());
        return "blog-create";
    }

    @PostMapping("/blog/new")
    public String createBlog(
            @AuthenticationPrincipal UserDetails user,
            @Valid @ModelAttribute("form") BlogCreateDto form,
            BindingResult br,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Model model
    ) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Blog yaratmaq üçün giriş etməlisən");
        }

        if (br.hasErrors()) {
            model.addAttribute("banner", bannerService.getBanner(BannerType.BLOGS));
            return "blog-create";
        }

        // ✅ burda EMAIL göndər
        String authorEmail = user.getUsername(); // səndə login usernameParameter=email
        Long id = blogService.createBlog(authorEmail, form, image);

        return "redirect:/blog/" + id;
    }

    // =========================
    // MY BLOGS (USER)
    // =========================
    @GetMapping("/my-blogs")
    public String myBlogs(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model
    ) {
        if (user == null) return "redirect:/auth/login";

        int size = 9;
        int pageIndex = Math.max(page - 1, 0);

        String myEmail = user.getUsername();

        var blogsPage = blogService.getMyBlogs(myEmail, pageIndex, size);

        model.addAttribute("banner", bannerService.getBanner(BannerType.BLOGS));
        model.addAttribute("blogs", blogsPage.getContent());
        model.addAttribute("currentPage", blogsPage.getNumber() + 1);
        model.addAttribute("totalPages", blogsPage.getTotalPages());

        return "my-blogs";
    }

    @PostMapping("/my-blogs/{id}/delete")
    public String deleteMyBlog(@PathVariable Long id, Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Giriş et");
        }
        blogService.deleteMyBlog(auth.getName(), id);
        return "redirect:/my-blogs?deleted=1";
    }


    // =========================
    // BLOG DETAIL
    // =========================
    @GetMapping("/blog/{id}")
    public String blogSingle(@PathVariable Long id, Model model, Authentication auth) {
        boolean isLoggedIn = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        fillBlogSingleModel(id, model, new BlogCommentCreateDto(), new BlogReplyCreateDto(), null, null, isLoggedIn);
        return "blog-single";
    }

    // ✅ Top-level comment (anonymous allowed)
    @PostMapping("/blog/{id}/comment")
    public String addComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user,
            @Valid @ModelAttribute("commentForm") BlogCommentCreateDto form,
            BindingResult br,
            Model model,
            Authentication auth
    ) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Şərh üçün giriş etməlisən");
        }

        if (br.hasErrors()) {
            boolean isLoggedIn = auth != null
                    && auth.isAuthenticated()
                    && !(auth instanceof AnonymousAuthenticationToken);

            fillBlogSingleModel(id, model, form, new BlogReplyCreateDto(), null, null, isLoggedIn);
            return "blog-single";
        }

        var blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog tapılmadı"));

        if (Boolean.FALSE.equals(blog.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog aktiv deyil");
        }

        var u = userRepository.findByEmailIgnoreCase(user.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));

        String fullName = (u.getFullName() != null && !u.getFullName().isBlank())
                ? u.getFullName().trim()
                : u.getEmail();

        BlogComment comment = new BlogComment();
        comment.setBlog(blog);
        comment.setParent(null);

        // ✅ profildən avtomatik
        comment.setFullName(fullName);
        comment.setEmail(u.getEmail());

        comment.setMessage(form.getMessage().trim());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setIsActive(true);

        blogCommentRepository.save(comment);

        return "redirect:/blog/" + id + "#comments";
    }

    // ✅ Reply (login required)
    @PostMapping("/blog/{id}/reply")
    public String addReply(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user,
            @Valid @ModelAttribute("replyForm") BlogReplyCreateDto form,
            BindingResult br,
            Model model,
            Authentication auth
    ) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Reply üçün giriş etməlisən");
        }

        boolean isLoggedIn = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        if (br.hasErrors()) {
            fillBlogSingleModel(id, model, new BlogCommentCreateDto(), form, null, null, isLoggedIn);
            return "blog-single";
        }

        var blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog tapılmadı"));

        if (Boolean.FALSE.equals(blog.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog aktiv deyil");
        }

        var parent = blogCommentRepository.findByIdAndBlog_IdAndIsActiveTrue(form.getParentId(), id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reply üçün parent comment tapılmadı"));

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

    private void fillBlogSingleModel(
            Long id,
            Model model,
            BlogCommentCreateDto commentForm,
            BlogReplyCreateDto replyForm,
            String search,
            String tag,
            boolean isLoggedIn
    ) {
        model.addAttribute("blog", blogService.getBlogDetail(id));
        model.addAttribute("banner", bannerService.getBanner(BannerType.BLOG_SINGLE));
        model.addAttribute("carCategories", carCategoryRepository.findAllWithActiveCarCount());
        model.addAttribute("recentBlogs", blogService.getRecentBlogs(id, 3));
        model.addAttribute("commentForm", commentForm);
        model.addAttribute("replyForm", replyForm);

        model.addAttribute("search", search);
        model.addAttribute("tag", tag);

        // ✅ UI üçün
        model.addAttribute("isLoggedIn", isLoggedIn);
    }
}