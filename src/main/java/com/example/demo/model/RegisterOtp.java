package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "register_otps",
        indexes = {
                @Index(name = "idx_reg_otp_email", columnList = "email")
        })
@Getter
@Setter
public class RegisterOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // eyni email üçün 1 aktiv record saxlayırıq
    @Column(nullable = false, unique = true, length = 190)
    private String email;

    // OTP plain saxlamırıq
    @Column(nullable = false, length = 255)
    private String otpHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Integer attempts = 0;

    @Column(nullable = false)
    private Boolean used = false;

    private LocalDateTime verifiedAt;
    private LocalDateTime usedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ✅ pending registration info (otp doğru olanda user yaradırıq)
    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 255)
    private String passwordHash;
}
