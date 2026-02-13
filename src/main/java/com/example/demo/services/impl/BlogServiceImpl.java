package com.example.demo.services.impl;

import com.example.demo.dto.blog.BlogCommentDto;
import com.example.demo.dto.blog.BlogCreateDto;
import com.example.demo.dto.blog.BlogDetailDto;
import com.example.demo.dto.blog.BlogListDto;
import com.example.demo.repository.BlogCommentRepository;
import com.example.demo.repository.BlogRepository;
import com.example.demo.services.BlogService;
import com.example.demo.services.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final BlogCommentRepository blogCommentRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Page<BlogListDto> getActiveBlogs(int page, int size, String search) {
        var pageable = PageRequest.of(page, size);

        var blogsPage = (search == null || search.isBlank())
                ? blogRepository.findAllByIsActiveTrueOrderByCreatedAtDesc(pageable)
                : blogRepository.searchActive(search.trim(), pageable);

        return blogsPage.map(this::toListDto);
    }

    @Override
    public Page<BlogListDto> getMyBlogs(String author, int page, int size) {
        var pageable = PageRequest.of(page, size);

        // ✅ My Blogs: istifadəçinin bütün bloqları (pending + active)
        var p = blogRepository.findAllByAuthorOrderByCreatedAtDesc(author, pageable);

        return p.map(this::toListDto);
    }

    @Override
    public Long createBlog(String author, BlogCreateDto dto, MultipartFile image) {
        String imageUrl = null;

        try {
            imageUrl = fileStorageService.storeBlogImage(image);

            var b = new com.example.demo.model.Blog();
            b.setTitle(dto.getTitle() == null ? null : dto.getTitle().trim());
            b.setShortDescription(dto.getShortDescription() == null ? null : dto.getShortDescription().trim());
            b.setContent(dto.getContent() == null ? null : dto.getContent().trim());

            b.setAuthor(author); // userDetails username/email
            b.setCreatedAt(LocalDate.now());
            b.setImageUrl(imageUrl);

            // ✅ təhlükəsizlik: əvvəlcə pending (admin təsdiqləyəcək)
            b.setIsActive(true);

            return blogRepository.save(b).getId();

        } catch (RuntimeException ex) {
            // upload olmuşdusa geri sil
            fileStorageService.deleteIfExists(imageUrl);
            throw ex;
        }
    }

    @Override
    public List<BlogListDto> getActiveBlogs() {
        // Home üçün 3 dənə aktiv blog
        var pageable = PageRequest.of(0, 3);

        return blogRepository.findRecentActiveBlogs(pageable)
                .stream()
                .map(this::toListDto)
                .toList();
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

        var all = blogCommentRepository.findAllByBlog_IdAndIsActiveTrueOrderByCreatedAtAsc(id);

        // 1) entity -> dto map
        var map = new java.util.LinkedHashMap<Long, BlogCommentDto>();
        for (var c : all) {
            BlogCommentDto cd = new BlogCommentDto();
            cd.setId(c.getId());
            cd.setFullName(c.getFullName());
            cd.setMessage(c.getMessage());
            cd.setCreatedAt(c.getCreatedAt());
            cd.setReplies(new java.util.ArrayList<>());
            map.put(c.getId(), cd);
        }

        // 2) parent-child bağla
        var roots = new java.util.ArrayList<BlogCommentDto>();
        for (var c : all) {
            var current = map.get(c.getId());
            if (c.getParent() == null) {
                roots.add(current);
            } else {
                var parentDto = map.get(c.getParent().getId());
                if (parentDto != null) parentDto.getReplies().add(current);
                else roots.add(current); // ehtiyat (DB-də parent silinibsə)
            }
        }

        // 3) root-ları DESC göstərmək istəyirsən: createdAt DESC
        roots.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        dto.setComments(roots);
        dto.setCommentCount(all.size());

        return dto;
    }

    @Override
    public List<BlogListDto> getRecentBlogs(Long excludeId, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 20);
        var pageable = PageRequest.of(0, safeLimit);

        var list = (excludeId == null)
                ? blogRepository.findRecentActiveBlogs(pageable)
                : blogRepository.findRecentActiveBlogsExclude(excludeId, pageable);

        return list.stream().map(this::toListDto).toList();
    }

    private BlogListDto toListDto(com.example.demo.model.Blog blog) {
        BlogListDto dto = new BlogListDto();
        dto.setId(blog.getId());
        dto.setTitle(blog.getTitle());
        dto.setShortDescription(blog.getShortDescription());
        dto.setImageUrl(blog.getImageUrl());
        dto.setAuthor(blog.getAuthor());
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setCommentCount(blogCommentRepository.countByBlog_IdAndIsActiveTrue(blog.getId()));
        return dto;
    }
}
