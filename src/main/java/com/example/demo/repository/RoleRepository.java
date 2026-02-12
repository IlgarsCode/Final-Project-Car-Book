package com.example.demo.repository;

import com.example.demo.model.Role;
import com.example.demo.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);

    List<Role> findAllByOrderByIdAsc();
}
