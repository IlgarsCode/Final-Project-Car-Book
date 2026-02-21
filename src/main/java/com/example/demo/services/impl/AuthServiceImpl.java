package com.example.demo.services.impl;

import com.example.demo.dto.auth.RegisterDto;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.enums.RoleName;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterDto dto) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Şifrə təkrarı uyğun deyil");
        }

        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu email artıq mövcuddur");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(RoleName.ROLE_USER);
                    return roleRepository.save(r);
                });

        User user = new User();
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setFullName(dto.getFullName().trim());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.getRoles().add(userRole);
        user.setIsActive(true);

        userRepository.save(user);
    }
}
