package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_otps",
        indexes = {
                @Index(name = "idx_pr_otp_email", columnList = "email"),
                @Index(name = "idx_pr_otp_token", columnList = "token")
        })
@Getter
@Setter
public class PasswordResetOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // email ilə də işləyirik (user_id də olar, amma səndə bu daha rahatdır)
    @Column(nullable = false, length = 190)
    private String email;

    // OTP-ni plain saxlamırıq
    @Column(nullable = false, length = 255)
    private String otpHash;

    // OTP verify ediləndən sonra reset üçün istifadə ediləcək token
    @Column(nullable = false, unique = true, length = 80)
    private String token;

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
}
