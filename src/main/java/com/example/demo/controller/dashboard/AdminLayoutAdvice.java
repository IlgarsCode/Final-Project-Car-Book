package com.example.demo.controller.dashboard;

import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
public class AdminLayoutAdvice {

    private final UserRepository userRepository;

    @ModelAttribute("currentUserFullName")
    public String currentUserFullName(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;

        String email = auth.getName(); // username = email
        return userRepository.findByEmailIgnoreCase(email)
                .map(u -> u.getFullName() != null ? u.getFullName() : u.getEmail())
                .orElse(email);
    }
}
