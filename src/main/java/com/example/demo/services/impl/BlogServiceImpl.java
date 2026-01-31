package com.example.demo.services.impl;

import com.example.demo.dto.blog.BlogCommentDto;
import com.example.demo.dto.blog.BlogDetailDto;
import com.example.demo.dto.blog.BlogListDto;
import com.example.demo.repository.BlogCommentRepository;
import com.example.demo.repository.BlogRepository;
import com.example.demo.services.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final BlogCommentRepository blogCommentRepository;

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

                    dto.setCommentCount(
                            blogCommentRepository
                                    .countByBlog_IdAndIsActiveTrue(blog.getId())
                    );
                    return dto;
                });
    }

    @Override
    public BlogDetailDto getBlogDetail(Long id) {
        var blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog tapılmadı"));

        if (Boolean.FALSE.equals(blog.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog aktiv deyil");
        }

        BlogDetailDto dto = new BlogDetailDto();
        dto.setId(blog.getId());
        dto.setTitle(blog.getTitle());
        dto.setAuthor(blog.getAuthor());
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setImageUrl(blog.getImageUrl());
        dto.setContent(blog.getContent());

        dto.setAuthorPhotoUrl(blog.getAuthorPhotoUrl());
        dto.setAuthorBio(blog.getAuthorBio());

        // ===== COMMENTS =====
        var comments = blogCommentRepository
                .findAllByBlog_IdAndIsActiveTrueOrderByCreatedAtDesc(id)
                .stream()
                .map(c -> {
                    BlogCommentDto cdto = new BlogCommentDto();
                    cdto.setId(c.getId());
                    cdto.setFullName(c.getFullName());
                    cdto.setMessage(c.getMessage());
                    cdto.setCreatedAt(c.getCreatedAt());
                    return cdto;
                })
                .toList();

        dto.setComments(comments);
        dto.setCommentCount(blogCommentRepository.countByBlog_IdAndIsActiveTrue(id));

        return dto;
    }

    @Override
    public Object getActiveBlogs() {
        // sən bunu artıq istifadə etmirsən, amma interface-də qalıb deyə boş saxladım
        return null;
    }
}
