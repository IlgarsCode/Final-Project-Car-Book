package com.example.demo.services;

import com.example.demo.dto.blog.BlogDetailDto;
import com.example.demo.dto.blog.BlogListDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BlogService {

    Page<BlogListDto> getActiveBlogs(int page, int size);

    BlogDetailDto getBlogDetail(Long id);

    // ✅ recent üçün
    List<BlogListDto> getRecentBlogs(Long excludeId, int limit);

    Object getActiveBlogs();
}
