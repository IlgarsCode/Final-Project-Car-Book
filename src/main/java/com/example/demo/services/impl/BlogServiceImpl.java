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

import java.util.List;

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
                            blogCommentRepository.countByBlog_IdAndIsActiveTrue(blog.getId())
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

    // ✅ RECENT BLOGS (sidebar)
    @Override
    public List<BlogListDto> getRecentBlogs(Long excludeId, int limit) {
        var pageable = PageRequest.of(0, limit);

        var list = (excludeId == null)
                ? blogRepository.findRecentActiveBlogs(pageable)
                : blogRepository.findRecentActiveBlogsExclude(excludeId, pageable);

        return list.stream()
                .map(b -> {
                    BlogListDto dto = new BlogListDto();
                    dto.setId(b.getId());
                    dto.setTitle(b.getTitle());
                    dto.setImageUrl(b.getImageUrl());
                    dto.setAuthor(b.getAuthor());
                    dto.setCreatedAt(b.getCreatedAt());

                    // istəsən comment sayını da göstərərik
                    dto.setCommentCount(blogCommentRepository.countByBlog_IdAndIsActiveTrue(b.getId()));
                    return dto;
                })
                .toList();
    }

    @Override
    public Object getActiveBlogs() {
        return null;
    }
}
