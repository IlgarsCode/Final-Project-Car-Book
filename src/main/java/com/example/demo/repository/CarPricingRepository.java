package com.example.demo.repository;

import com.example.demo.model.CarPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarPricingRepository extends JpaRepository<CarPricing, Long> {

    Optional<CarPricing> findByCar_Id(Long carId);

    boolean existsByCar_Id(Long carId);
    void deleteByCar_Id(Long carId);

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

    @Query("""
        select cp
        from CarPricing cp
        join fetch cp.car c
        where cp.isActive = true
          and c.isActive = true
          and c.id in :carIds
    """)
    List<CarPricing> findActiveByCarIds(@Param("carIds") List<Long> carIds);

    @Query("""
        select cp
        from CarPricing cp
        join fetch cp.car c
        where cp.isActive = true
          and c.isActive = true
          and c.slug = :slug
    """)
    Optional<CarPricing> findActiveByCarSlug(@Param("slug") String slug);

    @Query("""
        select cp
        from CarPricing cp
        join fetch cp.car c
        where cp.isActive = true
          and c.isActive = true
          and c.id = :carId
    """)
    Optional<CarPricing> findActiveByCarId(@Param("carId") Long carId);

    @Query("""
    select cp
    from CarPricing cp
    join fetch cp.car c
    join c.segment s
    where cp.isActive = true
      and c.isActive = true
      and s.slug = :segmentSlug
    order by c.id desc
""")
    List<CarPricing> findActivePricingRowsBySegmentSlug(@Param("segmentSlug") String segmentSlug);

    @Query("""
    select cp
    from CarPricing cp
    join fetch cp.car c
    join c.category cat
    join c.segment s
    where cp.isActive = true
      and c.isActive = true
      and cat.slug = :categorySlug
      and s.slug = :segmentSlug
    order by c.id desc
""")
    List<CarPricing> findActivePricingRowsByCategoryAndSegment(@Param("categorySlug") String categorySlug,
                                                               @Param("segmentSlug") String segmentSlug);
}
