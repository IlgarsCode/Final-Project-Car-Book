package com.example.demo.repository;

import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    Optional<Order> findByIdAndUser_Id(Long id, Long userId);

    @EntityGraph(attributePaths = {"user", "items", "items.car"})
    Optional<Order> findWithItemsById(Long id);

    // ✅ SƏNƏ LAZIM OLAN: userId + userOrderNo ilə detail (items-larla)
    @EntityGraph(attributePaths = {"items", "items.car"})
    Optional<Order> findWithItemsByUser_IdAndUserOrderNo(Long userId, Long userOrderNo);

    @Query("select coalesce(max(o.userOrderNo), 0) from Order o where o.user.id = :userId")
    long findMaxUserOrderNo(@Param("userId") Long userId);

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

    long countByStatus(OrderStatus status);

    @Query("""
        select o.status as status, count(o) as cnt
        from Order o
        group by o.status
    """)
    List<StatusCountView> countByStatusGroup();

    interface StatusCountView {
        OrderStatus getStatus();
        long getCnt();
    }

    @Query("""
        select o
        from Order o
        order by o.createdAt desc
    """)
    List<Order> findLatest(Pageable pageable);

    @Query("""
        select count(o)
        from Order o
        where o.status = :status and o.createdAt between :from and :to
    """)
    long countByStatusBetween(@Param("status") OrderStatus status,
                              @Param("from") LocalDateTime from,
                              @Param("to") LocalDateTime to);
}