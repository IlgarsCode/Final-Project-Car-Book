package com.example.demo.services.admin.impl;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.enums.RoleName;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.admin.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public java.util.List<User> getAllUsers() {
        return userRepository.findAllByOrderByIdDesc();
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User tapılmadı: " + id));
    }

    @Override
    @Transactional
    public void toggleActive(Long id) {
        User u = getUser(id);
        u.setIsActive(!Boolean.TRUE.equals(u.getIsActive()));
    }

    @Override
    @Transactional
    public void updateRoles(Long userId, Set<RoleName> roles) {
        User u = getUser(userId);

        // boş gəlməsin deyə ən az ROLE_USER saxla
        if (roles == null || roles.isEmpty()) {
            roles = Set.of(RoleName.ROLE_USER);
        }

        Set<Role> roleEntities = roles.stream()
                .map(rn -> roleRepository.findByName(rn)
                        .orElseThrow(() -> new RuntimeException("Role DB-də yoxdur: " + rn)))
                .collect(Collectors.toSet());

        u.setRoles(roleEntities);
    }
}
