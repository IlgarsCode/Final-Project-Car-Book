package com.example.demo.repository;

import com.example.demo.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByIntentId(String intentId);
    Optional<Payment> findTopByOrder_IdOrderByIdDesc(Long orderId);
}
