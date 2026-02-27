package com.example.demo.controller.dashboard;

import com.example.demo.dto.banner.*;
import com.example.demo.services.BannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/banner")
public class BannerAdminController {

    private final BannerService bannerService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("banners", bannerService.getAll());
        return "dashboard/banner/index";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("banner", new BannerCreateDto());
        return "dashboard/banner/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("banner") BannerCreateDto dto,
                         BindingResult br,
                         @RequestParam(name = "photo", required = false) MultipartFile photo) {
        if (br.hasErrors()) return "dashboard/banner/create";

        bannerService.create(dto, photo);
        return "redirect:/dashboard/banner";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        model.addAttribute("banner", bannerService.getByIdForUpdate(id));
        return "dashboard/banner/update";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("banner") BannerUpdateDto dto,
                         BindingResult br,
                         @RequestParam(name = "photo", required = false) MultipartFile photo) {
        if (br.hasErrors()) return "dashboard/banner/update";

        bannerService.update(dto, photo);
        return "redirect:/dashboard/banner";
    }

    @GetMapping("/delete/{id}")
    public String deleteForm(@PathVariable Long id, Model model) {
        BannerDeleteDto dto = new BannerDeleteDto();
        dto.setId(id);
        model.addAttribute("banner", dto);
        return "dashboard/banner/delete";
    }

    @PostMapping("/delete")
    public String delete(@Valid @ModelAttribute("banner") BannerDeleteDto dto,
                         BindingResult br) {
        if (br.hasErrors()) return "dashboard/banner/delete";

        bannerService.delete(dto);
        return "redirect:/dashboard/banner";
    }
}
