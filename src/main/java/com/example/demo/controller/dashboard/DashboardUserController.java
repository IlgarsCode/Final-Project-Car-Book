package com.example.demo.controller.dashboard;

import com.example.demo.model.enums.RoleName;
import com.example.demo.services.admin.RoleAdminService;
import com.example.demo.services.admin.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/users")
public class DashboardUserController {

    private final UserAdminService userAdminService;
    private final RoleAdminService roleAdminService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userAdminService.getAllUsers());
        return "dashboard/users/list";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        userAdminService.toggleActive(id);
        return "redirect:/dashboard/users";
    }

    @GetMapping("/{id}/roles")
    public String editRoles(@PathVariable Long id, Model model) {
        var user = userAdminService.getUser(id);

        Set<RoleName> selected = user.getRoles().stream()
                .map(r -> r.getName())
                .collect(java.util.stream.Collectors.toSet());

        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleAdminService.getAllRoles());
        model.addAttribute("selectedRoles", selected);
        return "dashboard/users/roles";
    }

    @PostMapping("/{id}/roles")
    public String updateRoles(@PathVariable Long id,
                              @RequestParam(value = "roles", required = false) Set<RoleName> roles) {
        userAdminService.updateRoles(id, roles);
        return "redirect:/dashboard/users";
    }
}
