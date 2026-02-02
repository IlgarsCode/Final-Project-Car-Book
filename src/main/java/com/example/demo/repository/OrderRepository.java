package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser_IdOrderByCreatedAtDesc(Long userId);
    Optional<Order> findByIdAndUser_Id(Long id, Long userId);
}
