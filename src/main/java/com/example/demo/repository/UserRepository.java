package com.example.demo.repository;

import com.example.demo.model.User;
import com.example.demo.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);

    // ✅ yeni: profil update zamanı "başqasının emaili var?" yoxlaması üçün
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<User> findAllByOrderByIdDesc();

    @Query("""
        select count(distinct u.id)
        from User u
        join u.roles r
        where u.isActive = true
          and r.name = :role
    """)
    long countActiveUsersByRole(@Param("role") RoleName role);
}
