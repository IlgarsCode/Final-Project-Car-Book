package com.example.demo.services.impl;

import com.example.demo.dto.blog.BlogListDto;
import com.example.demo.repository.BlogRepository;
import com.example.demo.services.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    @Override
    public List<BlogListDto> getActiveBlogs() {
        return blogRepository.findAllByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(blog -> {
                    BlogListDto dto = new BlogListDto();
                    dto.setId(blog.getId());
                    dto.setTitle(blog.getTitle());
                    dto.setShortDescription(blog.getShortDescription());
                    dto.setImageUrl(blog.getImageUrl());
                    dto.setAuthor(blog.getAuthor());
                    dto.setCreatedAt(blog.getCreatedAt());
                    return dto;
                })
                .toList();
    }
}
