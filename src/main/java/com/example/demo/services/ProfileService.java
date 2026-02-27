package com.example.demo.services;

import com.example.demo.dto.profile.PasswordChangeDto;
import com.example.demo.dto.profile.ProfileUpdateDto;
import com.example.demo.model.User;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    User getMe(String email);
    void updateProfile(String currentEmail, ProfileUpdateDto dto);
    void updateAvatar(String currentEmail, MultipartFile avatar);
    void changePassword(String currentEmail, PasswordChangeDto dto);
}
