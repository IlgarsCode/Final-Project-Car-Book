package com.example.demo.repository;

import com.example.demo.model.CarPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarPricingRepository extends JpaRepository<CarPricing, Long> {

    // Pricing page üçün (hamısı)
    @Query("""
        select cp
        from CarPricing cp
        join fetch cp.car c
        where cp.isActive = true and c.isActive = true
        order by c.id desc
    """)
    List<CarPricing> findActivePricingRows();

    // Pricing page filter üçün (category)
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

    // ✅ Car list / related üçün bulk pricing (N+1 olmasın)
    @Query("""
        select cp
        from CarPricing cp
        join fetch cp.car c
        where cp.isActive = true
          and c.isActive = true
          and c.id in :carIds
    """)
    List<CarPricing> findActiveByCarIds(@Param("carIds") List<Long> carIds);

    // ✅ Car detail üçün slug ilə pricing
    @Query("""
        select cp
        from CarPricing cp
        join fetch cp.car c
        where cp.isActive = true
          and c.isActive = true
          and c.slug = :slug
    """)
    Optional<CarPricing> findActiveByCarSlug(@Param("slug") String slug);

    // ✅ Cart-a add edəndə carId ilə pricing
    @Query("""
        select cp
        from CarPricing cp
        join fetch cp.car c
        where cp.isActive = true
          and c.isActive = true
          and c.id = :carId
    """)
    Optional<CarPricing> findActiveByCarId(@Param("carId") Long carId);
}
