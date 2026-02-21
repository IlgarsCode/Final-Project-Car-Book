package com.example.demo.services.impl;

import com.example.demo.model.PasswordResetOtp;
import com.example.demo.repository.PasswordResetOtpRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.AuthMailService;
import com.example.demo.services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMailService authMailService;

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int OTP_EXPIRE_MIN = 5;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RATE_LIMIT_SECONDS = 60;
    private static final int DAILY_LIMIT = 10;

    @Override
    public void requestOtp(String emailRaw) {
        String email = normalizeEmail(emailRaw);

        boolean exists = userRepository.existsByEmailIgnoreCase(email);

        LocalDateTime now = LocalDateTime.now();

        otpRepository.findTopByEmailIgnoreCaseOrderByIdDesc(email)
                .ifPresent(last -> {
                    if (last.getCreatedAt() != null && last.getCreatedAt().isAfter(now.minusSeconds(RATE_LIMIT_SECONDS))) {
                        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Bir az gözlə və yenidən yoxla");
                    }
                });

        long todayCount = otpRepository.countByEmailIgnoreCaseAndCreatedAtAfter(email, now.minusHours(24));
        if (todayCount >= DAILY_LIMIT) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Bu gün üçün limit dolub");
        }

        if (!exists) return;

        String otp = generate6DigitOtp();
        String token = UUID.randomUUID().toString().replace("-", "");

        PasswordResetOtp row = new PasswordResetOtp();
        row.setEmail(email);
        row.setOtpHash(passwordEncoder.encode(otp));
        row.setToken(token);
        row.setExpiresAt(now.plusMinutes(OTP_EXPIRE_MIN));
        row.setAttempts(0);
        row.setUsed(false);

        otpRepository.save(row);

        authMailService.sendPasswordResetOtp(email, otp, OTP_EXPIRE_MIN);
    }

    @Override
    public String verifyOtp(String emailRaw, String code) {
        String email = normalizeEmail(emailRaw);

        PasswordResetOtp row = otpRepository.findTopByEmailIgnoreCaseOrderByIdDesc(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP yanlışdır və ya vaxtı bitib"));

        if (Boolean.TRUE.equals(row.getUsed())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu OTP artıq istifadə olunub");
        }

        if (row.getExpiresAt() == null || row.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP vaxtı bitib");
        }

        if (row.getAttempts() != null && row.getAttempts() >= MAX_ATTEMPTS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Çox cəhd etdin, yeni OTP istə");
        }

        row.setAttempts((row.getAttempts() == null ? 0 : row.getAttempts()) + 1);

        boolean ok = passwordEncoder.matches(code, row.getOtpHash());
        if (!ok) {
            otpRepository.save(row);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP yanlışdır");
        }

        row.setVerifiedAt(LocalDateTime.now());
        otpRepository.save(row);

        return row.getToken();
    }

    @Override
    public void resetPassword(String token, String newPassword, String confirmNewPassword) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token boş ola bilməz");
        }
        if (newPassword == null || confirmNewPassword == null || !newPassword.equals(confirmNewPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Şifrə təkrarı uyğun deyil");
        }

        PasswordResetOtp row = otpRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token yanlışdır"));

        if (Boolean.TRUE.equals(row.getUsed())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token artıq istifadə olunub");
        }

        if (row.getExpiresAt() == null || row.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token vaxtı bitib, yenidən OTP istə");
        }

        String email = row.getEmail();

        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User tapılmadı"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        row.setUsed(true);
        row.setUsedAt(LocalDateTime.now());
        otpRepository.save(row);
    }

    private static String generate6DigitOtp() {
        int n = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(n);
    }

    private static String normalizeEmail(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase();
    }
}
