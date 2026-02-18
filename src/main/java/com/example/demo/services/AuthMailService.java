package com.example.demo.services;

public interface AuthMailService {
    void sendPasswordResetOtp(String toEmail, String otpCode, int expiresMinutes);
}
