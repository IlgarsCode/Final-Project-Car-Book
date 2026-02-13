package com.example.demo.services.impl;

import com.example.demo.dto.blog.*;
import com.example.demo.repository.BlogCommentRepository;
import com.example.demo.repository.BlogRepository;
import com.example.demo.repository.TagRepository;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final BlogCommentRepository blogCommentRepository;
    private final FileStorageService fileStorageService;
    private final TagRepository tagRepository;

    @Override
    public Page<BlogListDto> getActiveBlogs(int page, int size, String search, String tag) {
        var pageable = PageRequest.of(page, size);

        Page<com.example.demo.model.Blog> blogsPage;

        if (tag != null && !tag.isBlank()) {
            blogsPage = blogRepository.findActiveByTagSlug(tag.trim(), pageable);
        } else if (search == null || search.isBlank()) {
            blogsPage = blogRepository.findAllByIsActiveTrueOrderByCreatedAtDesc(pageable);
        } else {
            blogsPage = blogRepository.searchActive(search.trim(), pageable);
        }

        return blogsPage.map(this::toListDto);
    }

    @Override
    public Page<BlogListDto> getMyBlogs(String author, int page, int size) {
        var pageable = PageRequest.of(page, size);
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

            b.setAuthor(author);
            b.setCreatedAt(LocalDate.now());
            b.setImageUrl(imageUrl);

            // ✅ auto publish (sən istədin)
            b.setIsActive(true);

            // ✅ TAGS: dto.getTags() -> DB-də tap/yarat -> blog-a bağla
            var tags = resolveTags(dto.getTags());
            b.setTags(tags);

            return blogRepository.save(b).getId();

        } catch (RuntimeException ex) {
            fileStorageService.deleteIfExists(imageUrl);
            throw ex;
        }
    }

    @Override
    public Page<BlogListDto> getActiveBlogs(int page, int size, String search) {
        return null;
    }

    @Override
    public List<BlogListDto> getActiveBlogs() {
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

        // ✅ TAGS dto-ya yüklə (blog-single üçün)
        var tags = (blog.getTags() == null) ? List.<TagDto>of()
                : blog.getTags().stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsActive()))
                .map(t -> {
                    TagDto td = new TagDto();
                    td.setName(t.getName());
                    td.setSlug(t.getSlug());
                    return td;
                })
                .toList();
        dto.setTags(tags);

        // ✅ COMMENTS TREE
        var all = blogCommentRepository.findAllByBlog_IdAndIsActiveTrueOrderByCreatedAtAsc(id);

        var map = new LinkedHashMap<Long, BlogCommentDto>();
        for (var c : all) {
            BlogCommentDto cd = new BlogCommentDto();
            cd.setId(c.getId());
            cd.setFullName(c.getFullName());
            cd.setMessage(c.getMessage());
            cd.setCreatedAt(c.getCreatedAt());
            cd.setReplies(new ArrayList<>());
            map.put(c.getId(), cd);
        }

        var roots = new ArrayList<BlogCommentDto>();
        for (var c : all) {
            var current = map.get(c.getId());
            if (c.getParent() == null) {
                roots.add(current);
            } else {
                var parentDto = map.get(c.getParent().getId());
                if (parentDto != null) parentDto.getReplies().add(current);
                else roots.add(current);
            }
        }

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

    // ================= TAG HELPERS =================

    private static String slugify(String s) {
        if (s == null) return null;
        String x = s.trim().toLowerCase();
        x = x.replaceAll("[^a-z0-9\\s-]", "");
        x = x.replaceAll("\\s+", "-");
        x = x.replaceAll("-{2,}", "-");
        return x;
    }

    private Set<com.example.demo.model.Tag> resolveTags(String tagsRaw) {
        if (tagsRaw == null || tagsRaw.isBlank()) return new HashSet<>();

        var parts = Arrays.stream(tagsRaw.split(","))
                .map(String::trim)
                .filter(p -> !p.isBlank())
                .limit(15)
                .toList();

        var result = new HashSet<com.example.demo.model.Tag>();

        for (String name : parts) {
            String slug = slugify(name);
            if (slug == null || slug.isBlank()) continue;

            var tag = tagRepository.findBySlugIgnoreCase(slug).orElseGet(() -> {
                var t = new com.example.demo.model.Tag();
                t.setSlug(slug);
                t.setName(name.length() > 60 ? name.substring(0, 60) : name);
                t.setIsActive(true);
                return tagRepository.save(t);
            });

            result.add(tag);
        }

        return result;
    }
}
