package com.example.demo.controller.web;

import com.example.demo.dto.contact.ContactDto;
import com.example.demo.dto.enums.BannerType;
import com.example.demo.model.ContactInfo;
import com.example.demo.services.BannerService;
import com.example.demo.services.ContactInfoService;
import com.example.demo.services.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;
    private final ContactInfoService contactInfoService;
    private final BannerService bannerService;

    @GetMapping("/contact")
    public String contactPage(Model model) {

        ContactInfo contactInfo = contactInfoService.getActiveForWeb();
        if (contactInfo == null) { // ehtiyat, normalda null olmamalıdır
            contactInfo = contactInfoService.getOrCreateSingleton();
        }

        model.addAttribute("banner", bannerService.getBanner(BannerType.CONTACT));
        model.addAttribute("contactInfo", contactInfo);

        // form üçün boş dto
        model.addAttribute("contact", new ContactDto());

        return "contact";
    }

    @PostMapping("/contact")
    public String sendMessage(@ModelAttribute("contact") ContactDto dto) {
        contactService.saveMessage(dto);
        return "redirect:/contact?success";
    }
}
