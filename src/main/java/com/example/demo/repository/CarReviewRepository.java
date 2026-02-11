package com.example.demo.repository;

import com.example.demo.model.CarReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarReviewRepository extends JpaRepository<CarReview, Long> {

    Page<CarReview> findByCar_IdAndIsActiveTrue(Long carId, Pageable pageable);

    List<CarReview> findAllByCar_IdAndIsActiveTrueOrderByCreatedAtDesc(Long carId);

    long countByCar_IdAndIsActiveTrue(Long carId);

    long countByCar_IdAndRatingAndIsActiveTrue(Long carId, Integer rating);

    boolean existsByCar_Id(Long carId);
    void deleteByCar_Id(Long carId);



    // ✅ Pricing üçün: carId-lərə görə average rating (bulk, N+1 yox)
    @Query("""
        select r.car.id as carId, coalesce(avg(r.rating), 0) as avgRating
        from CarReview r
        where r.isActive = true and r.car.id in :carIds
        group by r.car.id
    """)
    List<CarAvgView> findAverageRatingsByCarIds(List<Long> carIds);

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
    List<CarCountView> countActiveReviewsByCarIds(List<Long> carIds);

    interface CarCountView {
        Long getCarId();
        long getReviewCount();
    }
}
