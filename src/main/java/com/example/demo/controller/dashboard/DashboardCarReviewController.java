package com.example.demo.controller.dashboard;

import com.example.demo.dto.car.CarReviewAdminFilterDto;
import com.example.demo.services.admin.CarReviewAdminService;
import com.example.demo.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/car-reviews")
public class DashboardCarReviewController {

    private final CarReviewAdminService carReviewAdminService;
    private final CarRepository carRepository;

    @GetMapping
    public String list(
            @ModelAttribute("filter") CarReviewAdminFilterDto filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        model.addAttribute("cars", carRepository.findAll());

        var reviewPage = carReviewAdminService.getPage(filter, page, size);
        model.addAttribute("page", reviewPage);

        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "dashboard/car-reviews/list";
    }

    // ✅ DETAIL PAGE
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("review", carReviewAdminService.getById(id));
        return "dashboard/car-reviews/detail";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id,
                         @ModelAttribute("filter") CarReviewAdminFilterDto filter,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size) {

        carReviewAdminService.toggleActive(id);
        return redirectWithParams(filter, page, size);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @ModelAttribute("filter") CarReviewAdminFilterDto filter,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size) {

        carReviewAdminService.hardDelete(id);
        return redirectWithParams(filter, page, size);
    }

    private String redirectWithParams(CarReviewAdminFilterDto f, int page, int size) {
        if (f == null) f = new CarReviewAdminFilterDto();

        String carId = f.getCarId() == null ? "" : f.getCarId().toString();
        String active = f.getActive() == null ? "" : f.getActive().toString();
        String rating = f.getRating() == null ? "" : f.getRating().toString();
        String q = f.getQ() == null ? "" : f.getQ();

        return "redirect:/dashboard/car-reviews?page=" + page + "&size=" + size
                + "&carId=" + carId
                + "&active=" + active
                + "&rating=" + rating
                + "&q=" + q;
    }
}
