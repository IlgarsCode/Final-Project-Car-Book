package com.example.demo.services.impl;

import com.example.demo.dto.blog.BlogListDto;
import com.example.demo.repository.BlogRepository;
import com.example.demo.services.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    @Override
    public Page<BlogListDto> getActiveBlogs(int page, int size) {

        var pageable = PageRequest.of(page, size);

        return blogRepository.findAllByIsActiveTrueOrderByCreatedAtDesc(pageable)
                .map(blog -> {
                    BlogListDto dto = new BlogListDto();
                    dto.setId(blog.getId());
                    dto.setTitle(blog.getTitle());
                    dto.setShortDescription(blog.getShortDescription());
                    dto.setImageUrl(blog.getImageUrl());
                    dto.setAuthor(blog.getAuthor());
                    dto.setCreatedAt(blog.getCreatedAt());
                    return dto;
                });
    }

    @Override
    public Object getActiveBlogs() {
        return null;
    }
}