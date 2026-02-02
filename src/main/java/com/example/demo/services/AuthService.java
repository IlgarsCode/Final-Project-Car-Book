package com.example.demo.services;

import com.example.demo.dto.auth.RegisterDto;

public interface AuthService {
    void register(RegisterDto dto);
}
