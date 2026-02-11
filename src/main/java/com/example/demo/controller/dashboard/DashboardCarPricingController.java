package com.example.demo.controller.dashboard;

import com.example.demo.dto.pricing.CarPricingCreateDto;
import com.example.demo.dto.pricing.CarPricingUpdateDto;
import com.example.demo.repository.CarPricingRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.services.admin.CarPricingAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/car-pricings")
public class DashboardCarPricingController {

    private final CarRepository carRepository;
    private final CarPricingRepository carPricingRepository;
    private final CarPricingAdminService carPricingAdminService;

    @GetMapping
    public String list(Model model) {

        model.addAttribute("cars", carRepository.findAll());                 // select üçün
        model.addAttribute("rows", carPricingRepository.findAll());          // table üçün

        // ✅ bunlar yoxdursa, th:field-lər “carId” tapa bilmir
        model.addAttribute("createForm", new CarPricingCreateDto());
        model.addAttribute("editForm", new CarPricingUpdateDto());

        return "dashboard/pricing/list";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("createForm") CarPricingCreateDto dto,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) {
            model.addAttribute("cars", carRepository.findAll());
            model.addAttribute("rows", carPricingRepository.findAll());
            model.addAttribute("editForm", new CarPricingUpdateDto());
            return "dashboard/pricing/list";
        }

        carPricingAdminService.createOrUpdate(dto);
        return "redirect:/dashboard/car-pricings";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("editForm") CarPricingUpdateDto dto,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) {
            model.addAttribute("cars", carRepository.findAll());
            model.addAttribute("rows", carPricingRepository.findAll());
            model.addAttribute("createForm", new CarPricingCreateDto());
            return "dashboard/pricing/list";
        }

        carPricingAdminService.update(dto);
        return "redirect:/dashboard/car-pricings";
    }

    @PostMapping("/deactivate/{carId}")
    public String deactivate(@PathVariable Long carId) {
        carPricingAdminService.deactivate(carId);
        return "redirect:/dashboard/car-pricings";
    }
}
