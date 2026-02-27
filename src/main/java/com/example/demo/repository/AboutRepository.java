package com.example.demo.repository;

import com.example.demo.model.About;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AboutRepository extends JpaRepository<About, Long> {
    About findByIsActiveTrue();
    About findTopByOrderByIdAsc();
}
