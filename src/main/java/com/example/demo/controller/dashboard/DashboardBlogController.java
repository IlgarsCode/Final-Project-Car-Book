package com.example.demo.controller.admin;

import com.example.demo.dto.blog.BlogAdminUpdateDto;
import com.example.demo.services.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/blogs")
public class DashboardBlogController {

    private final BlogService blogService;

    @GetMapping
    public String list(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "active", required = false) Boolean active,
            Model model
    ) {
        int size = 10;
        int pageIndex = Math.max(page - 1, 0);

        var p = blogService.adminGetBlogs(pageIndex, size, search, tag, active);

        model.addAttribute("blogs", p.getContent());
        model.addAttribute("currentPage", p.getNumber() + 1);
        model.addAttribute("totalPages", p.getTotalPages());

        model.addAttribute("search", search);
        model.addAttribute("tag", tag);
        model.addAttribute("active", active);

        return "dashboard/blog/blog-list";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("form", blogService.adminGetEditForm(id));
        return "dashboard/blog/blog-edit";
    }

    @PostMapping("/{id}/edit")
    public String doEdit(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") BlogAdminUpdateDto form,
            BindingResult br,
            @RequestParam(name = "image", required = false) MultipartFile image,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("id", id);
            return "dashboard/blog/blog-edit";
        }

        blogService.adminUpdateBlog(id, form, image);
        return "redirect:/dashboard/blogs?updated=" + id;
    }

    @PostMapping("/{id}/activate")
    public String activate(@PathVariable Long id,
                           @RequestParam(name = "back", required = false, defaultValue = "/dashboard/blogs") String back) {
        blogService.adminSetActive(id, true);
        return "redirect:" + back;
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Long id,
                             @RequestParam(name = "back", required = false, defaultValue = "/dashboard/blogs") String back) {
        blogService.adminSetActive(id, false);
        return "redirect:" + back;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        blogService.adminDeleteBlog(id);
        return "redirect:/dashboard/blogs?deleted=" + id;
    }
}
