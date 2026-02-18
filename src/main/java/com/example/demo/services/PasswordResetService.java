package com.example.demo.services;

public interface PasswordResetService {
    void requestOtp(String email);
    String verifyOtp(String email, String code); // success -> token qaytarır
    void resetPassword(String token, String newPassword, String confirmNewPassword);
}
