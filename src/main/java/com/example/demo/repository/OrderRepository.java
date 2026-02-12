package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findAllByUser_IdOrderByCreatedAtDesc(Long userId);
    Optional<Order> findByIdAndUser_Id(Long id, Long userId);

    @EntityGraph(attributePaths = {"user", "items", "items.car"})
    Optional<Order> findWithItemsById(Long id);

    @Query("select coalesce(max(o.userOrderNo), 0) from Order o where o.user.id = :userId")
    long findMaxUserOrderNo(@Param("userId") Long userId);
}
