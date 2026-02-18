package com.example.demo.repository;

import com.example.demo.model.RegisterOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RegisterOtpRepository extends JpaRepository<RegisterOtp, Long> {

    Optional<RegisterOtp> findByEmailIgnoreCase(String email);

    void deleteByExpiresAtBefore(LocalDateTime now);
}
