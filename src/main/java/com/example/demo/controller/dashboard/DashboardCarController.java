package com.example.demo.controller.dashboard;

import com.example.demo.dto.car.CarCreateDto;
import com.example.demo.dto.dashboard.car.CarUpdateDto;
import com.example.demo.services.admin.CarAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/cars")
public class DashboardCarController {

    private final CarAdminService carAdminService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("cars", carAdminService.getAll());
        return "dashboard/cars/list";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("form", new CarCreateDto());
        return "dashboard/cars/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("form") CarCreateDto form,
                         BindingResult br,
                         @RequestParam(name = "image", required = false) MultipartFile image) {

        if (br.hasErrors()) return "dashboard/cars/create";

        carAdminService.create(form, image);
        return "redirect:/dashboard/cars";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        var car = carAdminService.getById(id);

        CarUpdateDto dto = new CarUpdateDto();
        dto.setTitle(car.getTitle());
        dto.setBrand(car.getBrand());
        dto.setPricePerDay(car.getPricePerDay());

        dto.setMileage(car.getMileage());
        dto.setTransmission(car.getTransmission());
        dto.setSeats(car.getSeats());
        dto.setLuggage(car.getLuggage());
        dto.setFuel(car.getFuel());

        dto.setDescription(car.getDescription());
        dto.setFeaturesCol1(car.getFeaturesCol1());
        dto.setFeaturesCol2(car.getFeaturesCol2());
        dto.setFeaturesCol3(car.getFeaturesCol3());

        dto.setIsActive(car.getIsActive());

        model.addAttribute("carId", id);
        model.addAttribute("carImage", car.getImageUrl());
        model.addAttribute("form", dto);

        return "dashboard/cars/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("form") CarUpdateDto form,
                       BindingResult br,
                       @RequestParam(name = "image", required = false) MultipartFile image,
                       Model model) {

        if (br.hasErrors()) {
            var car = carAdminService.getById(id);
            model.addAttribute("carId", id);
            model.addAttribute("carImage", car.getImageUrl());
            return "dashboard/cars/edit";
        }

        carAdminService.update(id, form, image);
        return "redirect:/dashboard/cars";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        carAdminService.delete(id);
        return "redirect:/dashboard/cars";
    }
}
