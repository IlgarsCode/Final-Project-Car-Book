package com.example.demo.repository;

import com.example.demo.model.CarCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarCategoryRepository extends JpaRepository<CarCategory, Long> {

    // Sidebar: bütün kateqoriyalar + aktiv maşın sayı
    @Query("""
        select
            cc.id as id,
            cc.name as name,
            cc.slug as slug,
            count(c.id) as carCount
        from CarCategory cc
        left join Car c on c.category.id = cc.id and c.isActive = true
        group by cc.id, cc.name, cc.slug
        order by cc.name asc
    """)
    List<CarCategoryCountView> findAllWithActiveCarCount();

    interface CarCategoryCountView {
        Long getId();
        String getName();
        String getSlug();
        long getCarCount();
    }
}
