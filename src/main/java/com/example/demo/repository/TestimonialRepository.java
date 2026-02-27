package com.example.demo.repository;

import com.example.demo.model.Testimonial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {

    List<Testimonial> findAllByIsActiveTrue();

    long countByIsActiveTrue();

    @Query("""
        select t
        from Testimonial t
        where (:active is null or t.isActive = :active)
          and (
            :q is null or :q = '' or
            lower(t.fullName) like lower(concat('%', :q, '%')) or
            lower(t.position) like lower(concat('%', :q, '%')) or
            lower(t.comment) like lower(concat('%', :q, '%'))
          )
        order by t.id desc
    """)
    Page<Testimonial> adminSearch(@Param("q") String q,
                                  @Param("active") Boolean active,
                                  Pageable pageable);
}
