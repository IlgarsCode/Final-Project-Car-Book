package com.example.demo.repository;

import com.example.demo.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<Car> findAllByIsActiveTrueAndSegment_SlugOrderByIdDesc(String segmentSlug);

    List<Car> findAllByIsActiveTrueAndCategory_SlugAndSegment_SlugOrderByIdDesc(String categorySlug, String segmentSlug);

    long countBySegment_Id(Long segmentId);

    long countBySegment_IdAndIsActiveTrue(Long segmentId);


    @Query("""
        select c
        from Car c
        where c.isActive = true
          and (:categorySlug is null or :categorySlug = '' or c.category.slug = :categorySlug)
          and (:segmentSlug is null or :segmentSlug = '' or c.segment.slug = :segmentSlug)
    """)
    Page<Car> findActiveCars(@Param("categorySlug") String categorySlug,
                             @Param("segmentSlug") String segmentSlug,
                             Pageable pageable);
}
