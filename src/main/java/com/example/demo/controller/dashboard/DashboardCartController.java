package com.example.demo.controller.dashboard;

import com.example.demo.dto.cart.CartAdminFilterDto;
import com.example.demo.services.admin.CartAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/carts")
public class DashboardCartController {

    private final CartAdminService cartAdminService;

    @GetMapping
    public String list(
            @ModelAttribute("filter") CartAdminFilterDto filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        model.addAttribute("page", cartAdminService.getPage(filter, page, size));
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        return "dashboard/carts/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("cart", cartAdminService.getDetail(id));
        return "dashboard/carts/detail";
    }

    @PostMapping("/{id}/clear")
    public String clear(@PathVariable Long id) {
        cartAdminService.clearCart(id);
        return "redirect:/dashboard/carts/" + id;
    }

    @PostMapping("/{cartId}/items/{itemId}/delete")
    public String removeItem(@PathVariable Long cartId, @PathVariable Long itemId) {
        cartAdminService.removeItem(cartId, itemId);
        return "redirect:/dashboard/carts/" + cartId;
    }
}
