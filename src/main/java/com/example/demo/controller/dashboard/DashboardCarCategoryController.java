package com.example.demo.controller.dashboard;

import com.example.demo.dto.car.CarCategoryCreateDto;
import com.example.demo.dto.car.CarCategoryUpdateDto;
import com.example.demo.services.admin.CarCategoryAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/car-categories")
public class DashboardCarCategoryController {

    private final CarCategoryAdminService carCategoryAdminService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rows", carCategoryAdminService.getAllRows());
        model.addAttribute("createForm", new CarCategoryCreateDto());
        return "dashboard/car-categories/list";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("createForm") CarCategoryCreateDto form,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("rows", carCategoryAdminService.getAllRows());
            return "dashboard/car-categories/list";
        }

        carCategoryAdminService.create(form);
        return "redirect:/dashboard/car-categories";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        var cc = carCategoryAdminService.getById(id);

        CarCategoryUpdateDto form = new CarCategoryUpdateDto();
        form.setName(cc.getName());

        model.addAttribute("id", id);
        model.addAttribute("editForm", form);
        return "dashboard/car-categories/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("editForm") CarCategoryUpdateDto form,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("id", id);
            return "dashboard/car-categories/edit";
        }

        carCategoryAdminService.update(id, form);
        return "redirect:/dashboard/car-categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        carCategoryAdminService.delete(id);
        return "redirect:/dashboard/car-categories";
    }
}
