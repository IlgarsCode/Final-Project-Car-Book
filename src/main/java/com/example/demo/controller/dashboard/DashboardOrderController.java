package com.example.demo.controller.dashboard;

import com.example.demo.dto.order.OrderAdminFilterDto;
import com.example.demo.model.OrderStatus;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.services.admin.OrderAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/orders")
public class DashboardOrderController {

    private final OrderAdminService orderAdminService;
    private final PaymentRepository paymentRepository; // ✅ NEW

    @GetMapping
    public String list(@ModelAttribute("filter") OrderAdminFilterDto filter,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {

        model.addAttribute("page", orderAdminService.getPage(filter, page, size));
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "dashboard/orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        var order = orderAdminService.getDetail(id);
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values());

        // ✅ Order-a bağlı ən son payment (yoxdursa null)
        var paymentOpt = paymentRepository.findTopByOrder_IdOrderByIdDesc(id);
        model.addAttribute("payment", paymentOpt.orElse(null));

        return "dashboard/orders/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status) {
        orderAdminService.updateStatus(id, status);
        return "redirect:/dashboard/orders/" + id;
    }

    @PostMapping("/{id}/delete")
    public String hardDelete(@PathVariable Long id) {
        orderAdminService.hardDelete(id);
        return "redirect:/dashboard/orders";
    }
}
