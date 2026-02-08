package com.example.demo.controller.admin;

import com.example.demo.model.ServiceEntity;
import com.example.demo.services.ServiceAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/services")
public class ServiceAdminController {

    private final ServiceAdminService serviceAdminService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("services", serviceAdminService.getAll());
        return "dashboard/services/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("service", new ServiceEntity());
        return "dashboard/services/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("service") ServiceEntity service) {
        serviceAdminService.create(service);
        return "redirect:/dashboard/services";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("service", serviceAdminService.getById(id));
        return "dashboard/services/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute("service") ServiceEntity service) {
        serviceAdminService.update(id, service);
        return "redirect:/dashboard/services";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        serviceAdminService.delete(id);
        return "redirect:/dashboard/services";
    }
}
