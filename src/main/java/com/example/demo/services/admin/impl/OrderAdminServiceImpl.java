package com.example.demo.services.admin.impl;

import com.example.demo.dto.order.*;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.User;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.services.admin.OrderAdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderAdminServiceImpl implements OrderAdminService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public Page<OrderDashboardDto> getPage(OrderAdminFilterDto filter, int page, int size) {

        final OrderAdminFilterDto f = (filter == null) ? new OrderAdminFilterDto() : filter;

        int p = Math.max(page, 0);
        int s = Math.min(Math.max(size, 5), 50);

        Specification<Order> spec = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            Join<Order, User> u = root.join("user");

            if (f.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), f.getStatus()));
            }

            if (f.getFrom() != null) {
                LocalDateTime fromDt = f.getFrom().atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDt));
            }

            if (f.getTo() != null) {
                LocalDateTime toDt = f.getTo().atTime(LocalTime.MAX);
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toDt));
            }

            if (f.getQ() != null && !f.getQ().isBlank()) {
                String like = "%" + f.getQ().toLowerCase().trim() + "%";
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(u.get("email")), like),
                                cb.like(cb.lower(root.get("pickupLocation")), like),
                                cb.like(cb.lower(root.get("dropoffLocation")), like)
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orderPage = orderRepository.findAll(spec, pageable);

        // ✅ itemCount bulk
        List<Long> orderIds = orderPage.getContent().stream().map(Order::getId).toList();
        Map<Long, Long> countMap = orderIds.isEmpty()
                ? Map.of()
                : orderItemRepository.countItemsByOrderIds(orderIds).stream()
                .collect(Collectors.toMap(OrderItemRepository.OrderCountView::getOrderId, OrderItemRepository.OrderCountView::getCnt));

        return orderPage.map(o -> toDashboardDto(o, countMap.getOrDefault(o.getId(), 0L)));
    }

    @Override
    public OrderDetailDashboardDto getDetail(Long orderId) {
        Order o = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order tapılmadı"));

        OrderDetailDashboardDto dto = new OrderDetailDashboardDto();
        dto.setId(o.getId());
        dto.setStatus(o.getStatus());
        dto.setUserEmail(o.getUser().getEmail());

        dto.setPickupLocation(o.getPickupLocation());
        dto.setDropoffLocation(o.getDropoffLocation());
        dto.setPickupDate(o.getPickupDate());
        dto.setDropoffDate(o.getDropoffDate());
        dto.setPickupTime(o.getPickupTime());
        dto.setRentalDays(o.getRentalDays());

        dto.setTotalAmount(o.getTotalAmount());
        dto.setCreatedAt(o.getCreatedAt());

        dto.setItems(
                o.getItems().stream().map(oi -> {
                    OrderItemDashboardDto x = new OrderItemDashboardDto();
                    x.setId(oi.getId());
                    x.setCarId(oi.getCar().getId());
                    x.setCarTitleSnapshot(oi.getCarTitleSnapshot());
                    x.setCarImageUrlSnapshot(oi.getCarImageUrlSnapshot());
                    x.setRateType(oi.getRateType());
                    x.setUnitPriceSnapshot(oi.getUnitPriceSnapshot());
                    x.setFuelSurchargePerHourSnapshot(oi.getFuelSurchargePerHourSnapshot());
                    x.setUnitCountSnapshot(oi.getUnitCountSnapshot());
                    x.setQuantity(oi.getQuantity());
                    x.setLineTotal(oi.getLineTotal());
                    return x;
                }).toList()
        );

        return dto;
    }

    @Override
    @Transactional
    public void updateStatus(Long orderId, OrderStatus status) {
        if (status == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status boş ola bilməz");

        Order o = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order tapılmadı"));

        o.setStatus(status);
        orderRepository.save(o);
    }

    @Override
    @Transactional
    public void hardDelete(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order tapılmadı");
        }
        orderRepository.deleteById(orderId);
    }

    private OrderDashboardDto toDashboardDto(Order o, long itemCount) {
        OrderDashboardDto dto = new OrderDashboardDto();
        dto.setId(o.getId());
        dto.setStatus(o.getStatus());
        dto.setUserEmail(o.getUser().getEmail());
        dto.setPickupLocation(o.getPickupLocation());
        dto.setDropoffLocation(o.getDropoffLocation());
        dto.setPickupDate(o.getPickupDate());
        dto.setDropoffDate(o.getDropoffDate());
        dto.setRentalDays(o.getRentalDays());
        dto.setTotalAmount(o.getTotalAmount());
        dto.setCreatedAt(o.getCreatedAt());
        dto.setItemCount(itemCount);
        return dto;
    }
}
