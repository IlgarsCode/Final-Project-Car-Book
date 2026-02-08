package com.example.demo.controller.dashboard;

import com.example.demo.dto.contact.ContactInfoUpdateDto;
import com.example.demo.model.ContactInfo;
import com.example.demo.services.ContactInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/contact-info")
public class ContactInfoAdminController {

    private final ContactInfoService contactInfoService;

    @GetMapping
    public String editPage(Model model) {
        ContactInfo info = contactInfoService.getOrCreateSingleton(); // null olmayacaq

        ContactInfoUpdateDto dto = new ContactInfoUpdateDto();
        dto.setAddress(info.getAddress());
        dto.setPhone(info.getPhone());
        dto.setEmail(info.getEmail());
        dto.setActive(info.isActive());

        // Template th:object="${contactInfo}" olacaqsa bu ad düz gəlir
        model.addAttribute("contactInfo", dto);

        // SƏNDƏKİ REAL YOL: templates/dashboard/contact/edit.html
        return "dashboard/contact/edit";
    }

    @PostMapping
    public String update(@ModelAttribute("contactInfo") ContactInfoUpdateDto dto) {
        contactInfoService.update(dto);
        return "redirect:/dashboard/contact-info?success";
    }
}