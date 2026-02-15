package com.example.demo.dto.availability;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AvailabilityResponseDto {
    private boolean available;
    private String message;
}
