package com.example.demo.controller.dashboard;

import com.example.demo.dto.testimonial.TestimonialAdminUpdateDto;
import com.example.demo.services.TestimonialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/testimonial")
public class TestimonialAdminController {

    private final TestimonialService testimonialService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String q,
                       @RequestParam(required = false) Boolean active,
                       Model model) {

        int pageIndex = Math.max(page - 1, 0);
        var p = testimonialService.adminGet(pageIndex, size, q, active);

        model.addAttribute("items", p.getContent());
        model.addAttribute("currentPage", p.getNumber() + 1);
        model.addAttribute("totalPages", p.getTotalPages());
        model.addAttribute("q", q);
        model.addAttribute("active", active);

        return "dashboard/testimonial/list";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("form", testimonialService.adminGetEditForm(id));
        return "dashboard/testimonial/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") TestimonialAdminUpdateDto form,
                         BindingResult br,
                         Model model) {
        if (br.hasErrors()) {
            model.addAttribute("id", id);
            return "dashboard/testimonial/edit";
        }
        testimonialService.adminUpdate(id, form);
        return "redirect:/dashboard/testimonial?updated=1";
    }

    @PostMapping("/{id}/active")
    public String setActive(@PathVariable Long id,
                            @RequestParam boolean active,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(required = false) String q,
                            @RequestParam(required = false) Boolean filterActive) {
        testimonialService.adminSetActive(id, active);
        // filterlər
        String qs = (q != null && !q.isBlank()) ? "&q=" + q : "";
        String as = (filterActive != null) ? "&active=" + filterActive : "";
        return "redirect:/dashboard/testimonial?page=" + page + qs + as;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        testimonialService.adminDelete(id);
        return "redirect:/dashboard/testimonial?deleted=1";
    }
}
