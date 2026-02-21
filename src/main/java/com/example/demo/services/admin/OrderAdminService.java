package com.example.demo.services.admin;

import com.example.demo.dto.order.OrderAdminFilterDto;
import com.example.demo.dto.order.OrderDetailDashboardDto;
import com.example.demo.dto.order.OrderDashboardDto;
import com.example.demo.model.OrderStatus;
import org.springframework.data.domain.Page;

public interface OrderAdminService {
    Page<OrderDashboardDto> getPage(OrderAdminFilterDto filter, int page, int size);
    OrderDetailDashboardDto getDetail(Long orderId);

    void updateStatus(Long orderId, OrderStatus status);

    void hardDelete(Long orderId);
}