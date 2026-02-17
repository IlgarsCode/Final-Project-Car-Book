package com.example.demo.controller.dashboard;

import com.example.demo.dto.location.LocationCreateDto;
import com.example.demo.dto.location.LocationUpdateDto;
import com.example.demo.model.Location;
import com.example.demo.services.admin.LocationAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/locations")
public class DashboardLocationController {

    private final LocationAdminService locationAdminService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rows", locationAdminService.getAll());
        model.addAttribute("createForm", new LocationCreateDto());
        return "dashboard/locations/list";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("createForm") LocationCreateDto form,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) {
            model.addAttribute("rows", locationAdminService.getAll());
            return "dashboard/locations/list";
        }

        locationAdminService.create(form);
        return "redirect:/dashboard/locations";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        Location l = locationAdminService.getById(id);

        LocationUpdateDto editForm = new LocationUpdateDto();
        editForm.setName(l.getName());
        editForm.setSortOrder(l.getSortOrder());
        editForm.setIsActive(l.getIsActive());

        model.addAttribute("id", id);
        model.addAttribute("editForm", editForm);
        return "dashboard/locations/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("editForm") LocationUpdateDto form,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) {
            model.addAttribute("id", id);
            return "dashboard/locations/edit";
        }

        locationAdminService.update(id, form);
        return "redirect:/dashboard/locations";
    }

    @PostMapping("/deactivate/{id}")
    public String deactivate(@PathVariable Long id) {
        locationAdminService.softDelete(id);
        return "redirect:/dashboard/locations";
    }

    @PostMapping("/activate/{id}")
    public String activate(@PathVariable Long id) {
        locationAdminService.activate(id);
        return "redirect:/dashboard/locations";
    }
}
