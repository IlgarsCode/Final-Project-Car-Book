package com.example.demo.controller.dashboard;

import com.example.demo.dto.about.AboutUpdateDto;
import com.example.demo.model.About;
import com.example.demo.services.AboutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/about")
public class AboutAdminController {

    private final AboutService aboutService;

    @GetMapping
    public String editPage(Model model) {
        About about = aboutService.getAbout();

        AboutUpdateDto dto = new AboutUpdateDto();
        dto.setPageTitle(about.getPageTitle());
        dto.setSectionTitle(about.getSectionTitle());
        dto.setDescription(about.getDescription());
        dto.setImageUrl(about.getImageUrl());

        model.addAttribute("dto", dto);
        model.addAttribute("currentImage", about.getImageUrl());
        return "dashboard/about/edit";
    }

    @PostMapping
    public String update(
            @ModelAttribute("dto") AboutUpdateDto dto,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        aboutService.update(dto, image);
        return "redirect:/dashboard/about";
    }
}
