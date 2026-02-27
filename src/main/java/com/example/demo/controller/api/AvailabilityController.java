package com.example.demo.controller.api;

import com.example.demo.dto.availability.AvailabilityResponseDto;
import com.example.demo.services.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping("/car/{carId}")
    public ResponseEntity<AvailabilityResponseDto> checkCar(
            @PathVariable Long carId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dropoffDate
    ) {
        if (pickupDate == null || dropoffDate == null) {
            return ResponseEntity.badRequest()
                    .body(new AvailabilityResponseDto(false, "Tarixlər boş ola bilməz"));
        }
        if (dropoffDate.isBefore(pickupDate)) {
            return ResponseEntity.badRequest()
                    .body(new AvailabilityResponseDto(false, "Dropoff pickup-dan əvvəl ola bilməz"));
        }

        boolean ok = availabilityService.isCarAvailable(carId, pickupDate, dropoffDate);

        return ResponseEntity.ok(ok
                ? new AvailabilityResponseDto(true, "Uyğundur")
                : new AvailabilityResponseDto(false, "İcarədədir"));
    }
}
