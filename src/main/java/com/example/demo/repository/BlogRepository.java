package com.example.demo.repository;

import com.example.demo.model.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    // ========= PUBLIC =========
    Page<Blog> findAllByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    Page<Blog> findAllByAuthorOrderByCreatedAtDesc(String author, Pageable pageable);

    @Query("""
        select b
        from Blog b
        where b.isActive = true
          and (
            lower(b.title) like lower(concat('%', :q, '%'))
            or lower(b.shortDescription) like lower(concat('%', :q, '%'))
            or lower(b.content) like lower(concat('%', :q, '%'))
            or lower(b.author) like lower(concat('%', :q, '%'))
          )
        order by b.createdAt desc
    """)
    Page<Blog> searchActive(@Param("q") String q, Pageable pageable);

    @Query("""
        select b
        from Blog b
        where b.isActive = true and b.id <> :excludeId
        order by b.createdAt desc
    """)
    List<Blog> findRecentActiveBlogsExclude(@Param("excludeId") Long excludeId, Pageable pageable);

    @Query("""
        select b
        from Blog b
        where b.isActive = true
        order by b.createdAt desc
    """)
    List<Blog> findRecentActiveBlogs(Pageable pageable);

    @Query("""
        select distinct b
        from Blog b
        join b.tags t
        where b.isActive = true
          and lower(t.slug) = lower(:slug)
        order by b.createdAt desc
    """)
    Page<Blog> findActiveByTagSlug(@Param("slug") String slug, Pageable pageable);

    // ========= ADMIN =========

    Page<Blog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Blog> findAllByIsActiveOrderByCreatedAtDesc(Boolean isActive, Pageable pageable);

    @Query("""
        select b
        from Blog b
        where (
            lower(b.title) like lower(concat('%', :q, '%'))
            or lower(b.shortDescription) like lower(concat('%', :q, '%'))
            or lower(b.content) like lower(concat('%', :q, '%'))
            or lower(b.author) like lower(concat('%', :q, '%'))
        )
        order by b.createdAt desc
    """)
    Page<Blog> searchAll(@Param("q") String q, Pageable pageable);

    @Query("""
        select b
        from Blog b
        where b.isActive = :active
          and (
            lower(b.title) like lower(concat('%', :q, '%'))
            or lower(b.shortDescription) like lower(concat('%', :q, '%'))
            or lower(b.content) like lower(concat('%', :q, '%'))
            or lower(b.author) like lower(concat('%', :q, '%'))
          )
        order by b.createdAt desc
    """)
    Page<Blog> searchAllByActive(@Param("q") String q, @Param("active") Boolean active, Pageable pageable);

    @Query("""
        select distinct b
        from Blog b
        join b.tags t
        where lower(t.slug) = lower(:slug)
        order by b.createdAt desc
    """)
    Page<Blog> findAllByTagSlug(@Param("slug") String slug, Pageable pageable);

    @Query("""
        select distinct b
        from Blog b
        join b.tags t
        where b.isActive = :active
          and lower(t.slug) = lower(:slug)
        order by b.createdAt desc
    """)
    Page<Blog> findAllByTagSlugAndActive(@Param("slug") String slug, @Param("active") Boolean active, Pageable pageable);
}
