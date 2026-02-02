package com.example.demo.repository;

import com.example.demo.model.CarPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.List;

public interface CarPricingRepository extends JpaRepository<CarPricing, Long> {

    @Query("""
        select cp
        from CarPricing cp
        join fetch cp.car c
        where cp.isActive = true and c.isActive = true
        order by c.id desc
    """)
    List<CarPricing> findActivePricingRows();

    @Query("""
        select cp
        from CarPricing cp
        join fetch cp.car c
        join c.category cat
        where cp.isActive = true
          and c.isActive = true
          and cat.slug = :categorySlug
        order by c.id desc
    """)
    List<CarPricing> findActivePricingRowsByCategorySlug(@Param("categorySlug") String categorySlug);

    // ✅ 1) Car detail üçün: slug ilə pricing tap
    @Query("""
        select cp
        from CarPricing cp
        join cp.car c
        where cp.isActive = true and c.isActive = true and c.slug = :slug
    """)
    Optional<CarPricing> findActiveByCarSlug(@Param("slug") String slug);

    // ✅ 2) Car list üçün: bulk dailyRate mapping (IN query)
    @Query("""
        select cp
        from CarPricing cp
        join fetch cp.car c
        where cp.isActive = true and c.isActive = true and c.id in :carIds
    """)
    List<CarPricing> findActiveByCarIds(@Param("carIds") Collection<Long> carIds);
}
