package com.example.demo.repository;

import com.example.demo.model.CarCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarCategoryRepository extends JpaRepository<CarCategory, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    // ✅ Thymeleaf üçün: c.name, c.slug, c.activeCarCount
    interface CategoryWithCount {
        String getName();
        String getSlug();
        Long getActiveCarCount();

        Long getId();

        long getCarCount();
    }

    @Query("""
        select
          cat.name as name,
          cat.slug as slug,
          count(c.id) as activeCarCount
        from CarCategory cat
        left join Car c
          on c.category = cat and c.isActive = true
        group by cat.id, cat.name, cat.slug
        order by cat.name asc
    """)
    List<CategoryWithCount> findAllWithActiveCarCount();
}
