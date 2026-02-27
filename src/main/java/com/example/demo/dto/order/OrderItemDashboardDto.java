package com.example.demo.dto.order;

import com.example.demo.dto.enums.PricingRateType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemDashboardDto {
    private Long id;

    private Long carId;
    private String carTitleSnapshot;
    private String carImageUrlSnapshot;

    private PricingRateType rateType;

    private BigDecimal unitPriceSnapshot;
    private BigDecimal fuelSurchargePerHourSnapshot;

    private Integer unitCountSnapshot;
    private Integer quantity;

    private BigDecimal lineTotal;
}
