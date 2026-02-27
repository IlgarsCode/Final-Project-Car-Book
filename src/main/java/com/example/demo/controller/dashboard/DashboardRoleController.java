package com.example.demo.controller.dashboard;

import com.example.demo.services.admin.RoleAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/roles")
public class DashboardRoleController {

    private final RoleAdminService roleAdminService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("roles", roleAdminService.getAllRoles());
        return "dashboard/roles/list";
    }
}
