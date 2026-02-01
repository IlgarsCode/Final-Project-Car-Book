package com.example.demo.repository;

import com.example.demo.model.CarPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}