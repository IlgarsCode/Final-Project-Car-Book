package com.example.demo.services;

import com.example.demo.dto.blog.BlogListDto;

import java.util.List;

public interface BlogService {
    List<BlogListDto> getActiveBlogs();
}
