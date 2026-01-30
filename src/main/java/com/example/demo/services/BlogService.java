package com.example.demo.services;

import com.example.demo.dto.blog.BlogListDto;
import org.springframework.data.domain.Page;

public interface BlogService {

    Page<BlogListDto> getActiveBlogs(int page, int size);

    Object getActiveBlogs();
}