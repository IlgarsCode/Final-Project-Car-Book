package com.example.demo.repository;

import com.example.demo.model.Blog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    // blog list page
    org.springframework.data.domain.Page<Blog> findAllByIsActiveTrueOrderByCreatedAtDesc(org.springframework.data.domain.Pageable pageable);


    // ✅ recent blogs (exclude current blog)
    @Query("""
        select b
        from Blog b
        where b.isActive = true and b.id <> :excludeId
        order by b.createdAt desc
    """)
    List<Blog> findRecentActiveBlogsExclude(Long excludeId, Pageable pageable);

    // (istəsən ayrıca lazım olar)
    @Query("""
        select b
        from Blog b
        where b.isActive = true
        order by b.createdAt desc
    """)
    List<Blog> findRecentActiveBlogs(Pageable pageable);
}
