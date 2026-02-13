package com.example.demo.repository;

import com.example.demo.model.BlogComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogCommentRepository extends JpaRepository<BlogComment, Long> {

    // ✅ yalnız üst commentlər (parent null)
    List<BlogComment> findAllByBlog_IdAndIsActiveTrueAndParentIsNullOrderByCreatedAtDesc(Long blogId);

    // ✅ reply-lar da daxil bütün commentlər (tree yığmaq üçün)
    List<BlogComment> findAllByBlog_IdAndIsActiveTrueOrderByCreatedAtAsc(Long blogId);

    long countByBlog_IdAndIsActiveTrue(Long blogId);

    // ✅ təhlükəsizlik üçün: parent yoxlaması
    Optional<BlogComment> findByIdAndBlog_IdAndIsActiveTrue(Long id, Long blogId);
}