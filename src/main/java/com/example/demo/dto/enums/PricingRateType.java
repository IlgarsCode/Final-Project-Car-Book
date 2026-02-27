package com.example.demo.dto.enums;

public enum PricingRateType {
    HOURLY, DAILY, LEASING;

    public static PricingRateType fromParam(String rate) {
        if (rate == null || rate.isBlank()) return DAILY;

        return switch (rate.trim().toLowerCase()) {
            case "hourly", "hour", "h" -> HOURLY;
            case "leasing", "lease", "month", "monthly", "m" -> LEASING;
            default -> DAILY;
        };
    }
}
