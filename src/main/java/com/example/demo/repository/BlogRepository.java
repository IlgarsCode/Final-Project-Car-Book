package com.example.demo.repository;

import com.example.demo.model.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    // Blog list page
    Page<Blog> findAllByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    Page<Blog> findAllByAuthorOrderByCreatedAtDesc(String author, Pageable pageable);

    // ✅ SEARCH (blog listdə)
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

    // ✅ Recent blogs (exclude current blog)
    @Query("""
        select b
        from Blog b
        where b.isActive = true and b.id <> :excludeId
        order by b.createdAt desc
    """)
    List<Blog> findRecentActiveBlogsExclude(@Param("excludeId") Long excludeId, Pageable pageable);

    // ✅ Recent blogs (no exclude)
    @Query("""
        select b
        from Blog b
        where b.isActive = true
        order by b.createdAt desc
    """)
    List<Blog> findRecentActiveBlogs(Pageable pageable);
}
