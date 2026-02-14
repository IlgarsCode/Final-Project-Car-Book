package com.example.demo.services;

import com.example.demo.dto.blog.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogService {

    // PUBLIC
    Page<BlogListDto> getActiveBlogs(int page, int size, String search);
    List<BlogListDto> getActiveBlogs();
    BlogDetailDto getBlogDetail(Long id);
    List<BlogListDto> getRecentBlogs(Long excludeId, int limit);
    Page<BlogListDto> getActiveBlogs(int page, int size, String search, String tag);

    // MY BLOGS
    Page<BlogListDto> getMyBlogs(String author, int page, int size);
    Long createBlog(String author, BlogCreateDto dto, MultipartFile image);

    // ADMIN
    Page<BlogAdminListDto> adminGetBlogs(int page, int size, String search, String tag, Boolean isActive);
    BlogAdminUpdateDto adminGetEditForm(Long id);
    void adminUpdateBlog(Long id, BlogAdminUpdateDto dto, MultipartFile image);
    void adminSetActive(Long id, boolean active);
    void adminDeleteBlog(Long id);

    void deleteMyBlog(String authorEmail, Long blogId);
}
