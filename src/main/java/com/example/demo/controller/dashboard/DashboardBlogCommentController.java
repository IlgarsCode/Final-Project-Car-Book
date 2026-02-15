package com.example.demo.controller.dashboard;

import com.example.demo.repository.BlogRepository;
import com.example.demo.services.admin.BlogCommentAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/blog-comment")
public class DashboardBlogCommentController {

    private final BlogCommentAdminService blogCommentAdminService;
    private final BlogRepository blogRepository;

    @GetMapping
    public String list(@RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "size", defaultValue = "20") int size,
                       @RequestParam(name = "q", required = false) String q,
                       @RequestParam(name = "blogId", required = false) Long blogId,
                       @RequestParam(name = "active", required = false) Boolean active,
                       @RequestParam(name = "rootOnly", defaultValue = "false") boolean rootOnly,
                       Model model) {

        int pageIndex = Math.max(page - 1, 0);

        var p = blogCommentAdminService.getComments(pageIndex, size, q, blogId, active, rootOnly);

        model.addAttribute("rows", p.getContent());
        model.addAttribute("currentPage", p.getNumber() + 1);
        model.addAttribute("totalPages", p.getTotalPages());
        model.addAttribute("totalItems", p.getTotalElements());

        // filters keep
        model.addAttribute("q", q);
        model.addAttribute("blogId", blogId);
        model.addAttribute("active", active);
        model.addAttribute("rootOnly", rootOnly);
        model.addAttribute("size", size);

        // dropdown blogs
        model.addAttribute("blogs", blogRepository.findAllByOrderByCreatedAtDesc(org.springframework.data.domain.PageRequest.of(0, 300)).getContent());

        return "dashboard/blog-comment/comment-list";
    }

    @PostMapping("/deactivate/{id}")
    public String deactivate(@PathVariable Long id,
                             @RequestParam(name = "redirect", required = false, defaultValue = "/dashboard/blog-comment") String redirect) {
        blogCommentAdminService.setActive(id, false);
        return "redirect:" + redirect;
    }

    @PostMapping("/activate/{id}")
    public String activate(@PathVariable Long id,
                           @RequestParam(name = "redirect", required = false, defaultValue = "/dashboard/blog-comment") String redirect) {
        blogCommentAdminService.setActive(id, true);
        return "redirect:" + redirect;
    }

    @PostMapping("/hard-delete/{id}")
    public String hardDelete(@PathVariable Long id,
                             @RequestParam(name = "redirect", required = false, defaultValue = "/dashboard/blog-comment") String redirect) {
        blogCommentAdminService.hardDelete(id);
        return "redirect:" + redirect;
    }
}
