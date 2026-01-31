package com.example.demo.repository;

import com.example.demo.model.BlogComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogCommentRepository extends JpaRepository<BlogComment, Long> {

    List<BlogComment> findAllByBlog_IdAndIsActiveTrueOrderByCreatedAtDesc(Long blogId);

    long countByBlog_IdAndIsActiveTrue(Long blogId);

}
