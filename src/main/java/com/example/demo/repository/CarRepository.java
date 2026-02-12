package com.example.demo.repository;

import com.example.demo.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAllByIsActiveTrueOrderByIdDesc();
    Optional<Car> findBySlugAndIsActiveTrue(String slug);
    List<Car> findAllByIsActiveTrueAndCategory_SlugOrderByIdDesc(String slug);

    boolean existsBySlug(String slug);

    long countByCategory_IdAndIsActiveTrue(Long id);

    long countByCategory_Id(Long id);
    long countByIsActiveTrue();


}