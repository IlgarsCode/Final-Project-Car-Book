package com.example.demo.services;

import com.example.demo.dto.auth.RegisterDto;

public interface RegisterOtpService {
    void requestRegisterOtp(RegisterDto dto);
    void verifyRegisterOtpAndCreateUser(String email, String code);
}
