package com.example.demo.services.impl;

import com.example.demo.model.OrderStatus;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.services.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final OrderItemRepository orderItemRepository;

    private static final List<OrderStatus> ACTIVE = List.of(
            OrderStatus.PENDING,
            OrderStatus.APPROVED
    );

    @Override
    public boolean isCarAvailable(Long carId, LocalDate pickupDate, LocalDate dropoffDate) {
        boolean busy = orderItemRepository.existsActiveRentalOverlap(
                carId, ACTIVE, pickupDate, dropoffDate
        );
        return !busy;
    }
}
