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

        model.addAttribute("cars", carRepository.findAll());
        model.addAttribute("rows", carPricingRepository.findAll());

        CarPricingCreateDto create = new CarPricingCreateDto();
        create.setHourlyDiscountActive(false);
        create.setDailyDiscountActive(false);
        create.setLeasingDiscountActive(false);

        CarPricingUpdateDto edit = new CarPricingUpdateDto();
        edit.setHourlyDiscountActive(false);
        edit.setDailyDiscountActive(false);
        edit.setLeasingDiscountActive(false);

        model.addAttribute("createForm", create);
        model.addAttribute("editForm", edit);

        return "dashboard/pricing/list";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("createForm") CarPricingCreateDto dto,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) {
            model.addAttribute("cars", carRepository.findAll());
            model.addAttribute("rows", carPricingRepository.findAll());

            CarPricingUpdateDto edit = new CarPricingUpdateDto();
            edit.setHourlyDiscountActive(false);
            edit.setDailyDiscountActive(false);
            edit.setLeasingDiscountActive(false);
            model.addAttribute("editForm", edit);

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

            CarPricingCreateDto create = new CarPricingCreateDto();
            create.setHourlyDiscountActive(false);
            create.setDailyDiscountActive(false);
            create.setLeasingDiscountActive(false);
            model.addAttribute("createForm", create);

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
