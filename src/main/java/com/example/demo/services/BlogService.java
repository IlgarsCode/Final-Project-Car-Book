package com.example.demo.services;

import com.example.demo.dto.blog.BlogCreateDto;
import com.example.demo.dto.blog.BlogDetailDto;
import com.example.demo.dto.blog.BlogListDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BlogService {

    Page<BlogListDto> getActiveBlogs(int page, int size, String search);

    // ✅ Home page üçün
    List<BlogListDto> getActiveBlogs();

    BlogDetailDto getBlogDetail(Long id);

    List<BlogListDto> getRecentBlogs(Long excludeId, int limit);

    Page<BlogListDto> getMyBlogs(String author, int page, int size);
    Long createBlog(String author, BlogCreateDto dto, org.springframework.web.multipart.MultipartFile image);
}
