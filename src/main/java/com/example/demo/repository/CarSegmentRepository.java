package com.example.demo.repository;

import com.example.demo.model.CarSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarSegmentRepository extends JpaRepository<CarSegment, Long> {

    boolean existsByNameIgnoreCase(String name);
    boolean existsBySlug(String slug);

    interface SegmentWithCount {
        Long getId();
        String getName();
        String getSlug();
        Long getActiveCarCount();
    }

    @Query("""
        select
          s.id as id,
          s.name as name,
          s.slug as slug,
          count(c.id) as activeCarCount
        from CarSegment s
        left join Car c
          on c.segment = s and c.isActive = true
        group by s.id, s.name, s.slug
        order by s.name asc
    """)
    List<SegmentWithCount> findAllWithActiveCarCount();
}
