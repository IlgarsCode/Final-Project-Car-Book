package com.example.demo.repository;

import com.example.demo.model.CarReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarReviewRepository extends JpaRepository<CarReview, Long> {

    Page<CarReview> findByCar_IdAndIsActiveTrue(Long carId, Pageable pageable);

    List<CarReview> findAllByCar_IdAndIsActiveTrueOrderByCreatedAtDesc(Long carId);

    long countByCar_IdAndIsActiveTrue(Long carId);

    long countByCar_IdAndRatingAndIsActiveTrue(Long carId, Integer rating);

    boolean existsByCar_Id(Long carId);

    void deleteByCar_Id(Long carId);

    // ✅ Admin filter + pagination
    @Query("""
        select r
        from CarReview r
        join r.car c
        where (:carId is null or c.id = :carId)
          and (:active is null or r.isActive = :active)
          and (:rating is null or r.rating = :rating)
          and (:q is null or :q = '' 
               or lower(r.fullName) like lower(concat('%', :q, '%'))
               or lower(r.message) like lower(concat('%', :q, '%'))
               or lower(coalesce(r.email,'')) like lower(concat('%', :q, '%'))
          )
        order by r.createdAt desc
    """)
    Page<CarReview> adminSearch(
            @Param("carId") Long carId,
            @Param("active") Boolean active,
            @Param("rating") Integer rating,
            @Param("q") String q,
            Pageable pageable
    );

    // ✅ Pricing üçün: carId-lərə görə average rating (bulk, N+1 yox)
    @Query("""
        select r.car.id as carId, coalesce(avg(r.rating), 0) as avgRating
        from CarReview r
        where r.isActive = true and r.car.id in :carIds
        group by r.car.id
    """)
    List<CarAvgView> findAverageRatingsByCarIds(@Param("carIds") List<Long> carIds);

    interface CarAvgView {
        Long getCarId();
        Double getAvgRating();
    }

    // ✅ Pricing üçün: carId-lərə görə review count (bulk)
    @Query("""
        select r.car.id as carId, count(r.id) as reviewCount
        from CarReview r
        where r.isActive = true and r.car.id in :carIds
        group by r.car.id
    """)
    List<CarCountView> countActiveReviewsByCarIds(@Param("carIds") List<Long> carIds);

    interface CarCountView {
        Long getCarId();
        long getReviewCount();
    }
}
