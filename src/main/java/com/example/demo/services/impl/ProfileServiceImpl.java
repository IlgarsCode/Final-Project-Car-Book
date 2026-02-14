package com.example.demo.services.impl;

import com.example.demo.dto.profile.PasswordChangeDto;
import com.example.demo.dto.profile.ProfileUpdateDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.ProfileService;
import com.example.demo.services.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Override
    public User getMe(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tapılmadı"));
    }

    @Override
    public void updateProfile(String currentEmail, ProfileUpdateDto dto) {
        User user = getMe(currentEmail);

        String newEmail = dto.getEmail().trim().toLowerCase();
        String newName = dto.getFullName().trim();
        String newPhone = (dto.getPhone() == null) ? null : dto.getPhone().trim();
        String newBio = (dto.getBio() == null) ? null : dto.getBio().trim();



        if (userRepository.existsByEmailIgnoreCaseAndIdNot(newEmail, user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu email artıq istifadə olunur");
        }

        user.setEmail(newEmail);
        user.setFullName(newName);
        user.setPhone((newPhone != null && newPhone.isBlank()) ? null : newPhone);
        user.setBio((newBio != null && newBio.isBlank()) ? null : newBio);
        userRepository.save(user);
    }

    @Override
    public void updateAvatar(String currentEmail, MultipartFile avatar) {
        User user = getMe(currentEmail);

        if (avatar == null || avatar.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Avatar seçilməyib");
        }

        // köhnəni sil
        fileStorageService.deleteIfExists(user.getPhotoUrl());

        // yenini saxla
        String path = fileStorageService.storeUserAvatar(avatar);
        user.setPhotoUrl(path);

        userRepository.save(user);
    }

    @Override
    public void changePassword(String currentEmail, PasswordChangeDto dto) {
        User user = getMe(currentEmail);

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Köhnə şifrə yanlışdır");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yeni şifrə təkrarı uyğun deyil");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}
