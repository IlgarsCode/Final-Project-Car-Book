package com.example.demo.services.admin;

import com.example.demo.model.User;
import com.example.demo.model.enums.RoleName;

import java.util.List;
import java.util.Set;

public interface UserAdminService {
    List<User> getAllUsers();
    User getUser(Long id);
    void toggleActive(Long id);
    void updateRoles(Long userId, Set<RoleName> roles);
}
