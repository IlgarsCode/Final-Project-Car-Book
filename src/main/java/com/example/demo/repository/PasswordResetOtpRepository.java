package com.example.demo.repository;

import com.example.demo.model.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    // ən son yaradılan aktiv OTP-ni götür
    Optional<PasswordResetOtp> findTopByEmailIgnoreCaseOrderByIdDesc(String email);

    Optional<PasswordResetOtp> findByToken(String token);

    long countByEmailIgnoreCaseAndCreatedAtAfter(String email, LocalDateTime after);
}
