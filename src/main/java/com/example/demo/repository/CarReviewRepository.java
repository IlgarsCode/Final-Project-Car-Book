package com.example.demo.repository;

import com.example.demo.model.CarReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarReviewRepository extends JpaRepository<CarReview, Long> {

    List<CarReview> findAllByCar_IdAndIsActiveTrueOrderByCreatedAtDesc(Long carId);

    long countByCar_IdAndIsActiveTrue(Long carId);

    long countByCar_IdAndRatingAndIsActiveTrue(Long carId, Integer rating);
}
