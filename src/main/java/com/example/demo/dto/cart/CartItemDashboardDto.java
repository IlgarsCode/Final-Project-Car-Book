package com.example.demo.dto.cart;

import com.example.demo.dto.enums.PricingRateType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CartItemDashboardDto {
    private Long id;

    private Long carId;
    private String carTitle;
    private String carSlug;

    private PricingRateType rateType;

    private BigDecimal unitPriceSnapshot;
    private BigDecimal fuelSurchargePerHourSnapshot;

    private Integer unitCount;
    private Integer quantity;

    private LocalDateTime addedAt;
}
