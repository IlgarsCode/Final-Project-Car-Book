package com.example.demo.repository;

import com.example.demo.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    interface OrderCountView {
        Long getOrderId();
        Long getCnt();
    }

    @Query("""
        select oi.order.id as orderId, coalesce(sum(oi.quantity),0) as cnt
        from OrderItem oi
        where oi.order.id in :orderIds
        group by oi.order.id
    """)
    List<OrderCountView> countItemsByOrderIds(@Param("orderIds") List<Long> orderIds);
}