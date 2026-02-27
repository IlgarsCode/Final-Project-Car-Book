package com.example.demo.services.impl;

import com.example.demo.dto.home.HomeStatsDto;
import com.example.demo.model.enums.RoleName;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.TestimonialRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.HomeStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeStatsServiceImpl implements HomeStatsService {

    private final CarRepository carRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TestimonialRepository testimonialRepository;

    @Override
    public HomeStatsDto getHomeStats() {

        long totalCars = carRepository.countByIsActiveTrue();

        long totalOrders = orderRepository.count();

        long happyCustomers =
                userRepository.countActiveUsersByRole(RoleName.ROLE_USER);

        long totalTestimonials =
                testimonialRepository.countByIsActiveTrue();

        return new HomeStatsDto(
                totalCars,
                totalOrders,
                happyCustomers,
                totalTestimonials
        );
    }
}
