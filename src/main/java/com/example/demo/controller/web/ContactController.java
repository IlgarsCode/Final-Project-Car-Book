package com.example.demo.controller.web;

import com.example.demo.dto.contact.ContactDto;
import com.example.demo.dto.enums.BannerType;
import com.example.demo.model.ContactInfo;
import com.example.demo.services.BannerService;
import com.example.demo.services.ContactInfoService;
import com.example.demo.services.ContactMessageService;
import com.example.demo.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class ContactController {

    private final ContactMessageService contactMessageService;
    private final ContactInfoService contactInfoService;
    private final BannerService bannerService;
    private final EmailService emailService;

    // ✅ CONTACT PAGE (GET) -> /elaqe
    @GetMapping("/elaqe")
    public String contactPage(Model model) {

        ContactInfo contactInfo = contactInfoService.getActiveForWeb();
        if (contactInfo == null) {
            contactInfo = contactInfoService.getOrCreateSingleton();
        }

        model.addAttribute("banner", bannerService.getBanner(BannerType.CONTACT));
        model.addAttribute("contactInfo", contactInfo);

        // form üçün boş dto
        model.addAttribute("contact", new ContactDto());

        return "contact";
    }

    // ✅ CONTACT PAGE (POST) -> /elaqe
    @PostMapping("/elaqe")
    public String sendMessage(@ModelAttribute("contact") ContactDto dto) {
        contactMessageService.saveMessage(dto);
        emailService.sendContactMail(dto);
        return "redirect:/elaqe?success";
    }
}