package com.example.demo.controller.dashboard;

import com.example.demo.dto.segment.CarSegmentCreateDto;
import com.example.demo.dto.segment.CarSegmentUpdateDto;
import com.example.demo.services.admin.CarSegmentAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/car-segments")
public class DashboardCarSegmentController {

    private final CarSegmentAdminService carSegmentAdminService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rows", carSegmentAdminService.getAllRows());
        model.addAttribute("createForm", new CarSegmentCreateDto());
        return "dashboard/car-segments/list";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("createForm") CarSegmentCreateDto form,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) {
            model.addAttribute("rows", carSegmentAdminService.getAllRows());
            return "dashboard/car-segments/list";
        }

        carSegmentAdminService.create(form);
        return "redirect:/dashboard/car-segments";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        var s = carSegmentAdminService.getById(id);

        CarSegmentUpdateDto form = new CarSegmentUpdateDto();
        form.setName(s.getName());

        model.addAttribute("id", id);
        model.addAttribute("editForm", form);
        return "dashboard/car-segments/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("editForm") CarSegmentUpdateDto form,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) {
            model.addAttribute("id", id);
            return "dashboard/car-segments/edit";
        }

        carSegmentAdminService.update(id, form);
        return "redirect:/dashboard/car-segments";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        carSegmentAdminService.delete(id);
        return "redirect:/dashboard/car-segments";
    }
}
