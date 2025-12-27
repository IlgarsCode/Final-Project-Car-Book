package com.example.demo.controller.web;

import com.example.demo.dto.contact.ContactDto;
import com.example.demo.enums.BannerType;
import com.example.demo.services.BannerService;
import com.example.demo.services.ContactInfoService;
import com.example.demo.services.ContactMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ContactController {

    private final BannerService bannerService;
    private final ContactMessageService contactMessageService;
    private final ContactInfoService contactInfoService;

    @GetMapping("/contact")
    public String contact(Model model) {

        model.addAttribute("banner",
                bannerService.getBanner(BannerType.CONTACT));

        model.addAttribute("contactInfo",
                contactInfoService.getContactInfo());

        model.addAttribute("contact", new ContactDto());

        return "contact";
    }

    @PostMapping("/contact")
    public String submitContact(@ModelAttribute("contact") ContactDto contactDto) {

        contactMessageService.saveAndSend(contactDto);

        return "redirect:/contact?success";
    }
}
