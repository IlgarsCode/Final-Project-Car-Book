package com.example.demo.repository;

import com.example.demo.model.BlogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlogCommentRepository extends JpaRepository<BlogComment, Long> {

    List<BlogComment> findAllByBlog_IdAndIsActiveTrueAndParentIsNullOrderByCreatedAtDesc(Long blogId);

    List<BlogComment> findAllByBlog_IdAndIsActiveTrueOrderByCreatedAtAsc(Long blogId);

    long countByBlog_IdAndIsActiveTrue(Long blogId);

    Optional<BlogComment> findByIdAndBlog_IdAndIsActiveTrue(Long id, Long blogId);

    //  ADMIN SEARCH (FULL FILTER)
    @Query("""
        select c
        from BlogComment c
        join c.blog b
        where (:blogId is null or b.id = :blogId)
          and (:active is null or c.isActive = :active)
          and (:rootOnly = false or c.parent is null)
          and (
            :q is null or :q = '' or
            lower(c.fullName) like lower(concat('%', :q, '%')) or
            lower(c.email) like lower(concat('%', :q, '%')) or
            lower(c.message) like lower(concat('%', :q, '%')) or
            lower(b.title) like lower(concat('%', :q, '%'))
          )
        """)
    Page<BlogComment> adminSearch(@Param("q") String q,
                                  @Param("blogId") Long blogId,
                                  @Param("active") Boolean active,
                                  @Param("rootOnly") boolean rootOnly,
                                  Pageable pageable);
}
