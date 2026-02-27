package com.example.demo.controller.dashboard;

import com.example.demo.model.ContactMessage;
import com.example.demo.services.ContactMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/contact-messages")
public class ContactMessageAdminController {

    private final ContactMessageService service;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean unread,
            Model model
    ) {
        Page<ContactMessage> messages = service.getPage(page, size, unread);

        model.addAttribute("messages", messages);
        model.addAttribute("unreadCount", service.countUnread());
        model.addAttribute("unread", unread);

        return "dashboard/contact-message/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        service.markAsRead(id);
        ContactMessage m = service.getById(id);

        model.addAttribute("m", m);
        model.addAttribute("unreadCount", service.countUnread());

        return "dashboard/contact-message/detail";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/dashboard/contact-messages?deleted";
    }
}
