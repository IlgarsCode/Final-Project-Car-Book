package com.example.demo.repository;

import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findAllByUser_IdOrderByCreatedAtDesc(Long userId);
    Optional<Order> findByIdAndUser_Id(Long id, Long userId);

    @EntityGraph(attributePaths = {"user", "items", "items.car"})
    Optional<Order> findWithItemsById(Long id);

    @Query("select coalesce(max(o.userOrderNo), 0) from Order o where o.user.id = :userId")
    long findMaxUserOrderNo(@Param("userId") Long userId);

    // ✅ pricing + availability üçün: bu tarix intervalında DOLU olan carId-lər
    // Overlap qaydası: o.pickupDate <= dropoff AND o.dropoffDate >= pickup
    @Query("""
        select distinct oi.car.id
        from OrderItem oi
        join oi.order o
        where oi.car.id in :carIds
          and o.status in :statuses
          and o.pickupDate <= :dropoffDate
          and o.dropoffDate >= :pickupDate
    """)
    List<Long> findBusyCarIdsInRange(
            @Param("carIds") List<Long> carIds,
            @Param("pickupDate") LocalDate pickupDate,
            @Param("dropoffDate") LocalDate dropoffDate,
            @Param("statuses") List<OrderStatus> statuses
    );
}
