package com.example.demo.services.impl;

import com.example.demo.dto.blog.*;
import com.example.demo.model.Blog;
import com.example.demo.repository.BlogCommentRepository;
import com.example.demo.repository.BlogRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.BlogService;
import com.example.demo.services.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final BlogCommentRepository blogCommentRepository;
    private final FileStorageService fileStorageService;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Override
    public Page<BlogListDto> getActiveBlogs(int page, int size, String search) {
        return getActiveBlogs(page, size, search, null);
    }

    @Override
    public Page<BlogListDto> getActiveBlogs(int page, int size, String search, String tag) {
        var pageable = PageRequest.of(page, size);

        Page<Blog> blogsPage;

        if (tag != null && !tag.isBlank()) {
            blogsPage = blogRepository.findActiveByTagSlug(tag.trim(), pageable);
        } else if (search == null || search.isBlank()) {
            blogsPage = blogRepository.findAllByIsActiveTrueOrderByCreatedAtDesc(pageable);
        } else {
            blogsPage = blogRepository.searchActive(search.trim(), pageable);
        }

        // ✅ sadə cache (səhifəlik)
        Map<Long, com.example.demo.model.User> userByIdCache = new HashMap<>();
        Map<String, com.example.demo.model.User> userByEmailCache = new HashMap<>();

        return blogsPage.map(b -> toListDto(b, userByIdCache, userByEmailCache));
    }

    @Override
    public List<BlogListDto> getActiveBlogs() {
        var pageable = PageRequest.of(0, 3);

        Map<Long, com.example.demo.model.User> userByIdCache = new HashMap<>();
        Map<String, com.example.demo.model.User> userByEmailCache = new HashMap<>();

        return blogRepository.findRecentActiveBlogs(pageable)
                .stream()
                .map(b -> toListDto(b, userByIdCache, userByEmailCache))
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
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setImageUrl(blog.getImageUrl());
        dto.setContent(blog.getContent());

        var uOpt = Optional.<com.example.demo.model.User>empty();

        if (blog.getAuthorId() != null) {
            uOpt = userRepository.findById(blog.getAuthorId());
        } else if (blog.getAuthor() != null && !blog.getAuthor().isBlank()) {
            uOpt = userRepository.findByEmailIgnoreCase(blog.getAuthor().trim());
        }

        if (uOpt.isPresent()) {
            var u = uOpt.get();
            dto.setAuthorName((u.getFullName() != null && !u.getFullName().isBlank())
                    ? u.getFullName().trim()
                    : u.getEmail());
            dto.setAuthorAvatarUrl((u.getPhotoUrl() != null && !u.getPhotoUrl().isBlank())
                    ? u.getPhotoUrl()
                    : "/images/person_1.jpg");
            dto.setAuthorBio((u.getBio() != null && !u.getBio().isBlank())
                    ? u.getBio().trim()
                    : "CarBook istifadəçisi");
            dto.setAuthor(u.getEmail());
        } else {
            dto.setAuthorName("CarBook istifadəçisi");
            dto.setAuthorAvatarUrl("/images/person_1.jpg");
            dto.setAuthorBio("CarBook istifadəçisi");
            dto.setAuthor(blog.getAuthor());
        }

        var tags = (blog.getTags() == null) ? List.<TagDto>of()
                : blog.getTags().stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsActive()))
                .map(t -> {
                    TagDto td = new TagDto();
                    td.setName(t.getName());
                    td.setSlug(t.getSlug());
                    return td;
                }).toList();
        dto.setTags(tags);

        var all = blogCommentRepository.findAllByBlog_IdAndIsActiveTrueOrderByCreatedAtAsc(id);

        Set<String> emails = new HashSet<>();
        for (var c : all) {
            if (c.getEmail() != null && !c.getEmail().isBlank()) {
                emails.add(c.getEmail().trim().toLowerCase());
            }
        }

        Map<String, String> avatarByEmail = new HashMap<>();
        if (!emails.isEmpty()) {
            List<com.example.demo.model.User> users = userRepository.findAllByEmailLowerIn(emails);
            for (com.example.demo.model.User u : users) {
                if (u.getEmail() == null) continue;

                String em = u.getEmail().trim().toLowerCase();
                String avatar = (u.getPhotoUrl() != null && !u.getPhotoUrl().isBlank())
                        ? u.getPhotoUrl()
                        : "/images/person_1.jpg";

                avatarByEmail.put(em, avatar);
            }
        }

        Map<Long, BlogCommentDto> map = new LinkedHashMap<>();
        for (var c : all) {
            if (c == null || c.getId() == null) continue;

            BlogCommentDto cd = new BlogCommentDto();
            cd.setId(c.getId());
            cd.setFullName(c.getFullName());
            cd.setMessage(c.getMessage());
            cd.setCreatedAt(c.getCreatedAt());
            cd.setReplies(new ArrayList<>());

            String em = (c.getEmail() == null) ? null : c.getEmail().trim().toLowerCase();
            String avatar = (em != null && avatarByEmail.containsKey(em))
                    ? avatarByEmail.get(em)
                    : "/images/person_1.jpg";
            cd.setAvatarUrl(avatar);

            map.put(c.getId(), cd);
        }

        List<BlogCommentDto> roots = new ArrayList<>();
        for (var c : all) {
            if (c == null || c.getId() == null) continue;

            BlogCommentDto current = map.get(c.getId());
            if (current == null) continue;

            if (c.getParent() == null) {
                roots.add(current);
            } else {
                Long parentId = c.getParent().getId();
                BlogCommentDto parentDto = (parentId == null) ? null : map.get(parentId);

                if (parentDto != null) {
                    parentDto.getReplies().add(current);
                } else {
                    roots.add(current);
                }
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

        Map<Long, com.example.demo.model.User> userByIdCache = new HashMap<>();
        Map<String, com.example.demo.model.User> userByEmailCache = new HashMap<>();

        return list.stream()
                .map(b -> toListDto(b, userByIdCache, userByEmailCache))
                .toList();
    }

    @Override
    public Page<BlogListDto> getMyBlogs(String authorEmail, int page, int size) {
        var pageable = PageRequest.of(page, size);

        Map<Long, com.example.demo.model.User> userByIdCache = new HashMap<>();
        Map<String, com.example.demo.model.User> userByEmailCache = new HashMap<>();

        return blogRepository.findAllByAuthorOrderByCreatedAtDesc(authorEmail.trim(), pageable)
                .map(b -> toListDto(b, userByIdCache, userByEmailCache));
    }

    @Override
    public Long createBlog(String authorEmail, BlogCreateDto dto, MultipartFile image) {
        String imageUrl = null;

        try {
            imageUrl = fileStorageService.storeBlogImage(image);

            var me = userRepository.findByEmailIgnoreCase(authorEmail.trim())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));

            var b = new Blog();
            b.setTitle(dto.getTitle().trim());
            b.setShortDescription(dto.getShortDescription().trim());
            b.setContent(dto.getContent().trim());

            b.setAuthor(authorEmail.trim().toLowerCase());
            b.setAuthorId(me.getId());

            // ⚠️ Əgər Blog.createdAt LocalDateTime-dırsa -> LocalDateTime.now() olmalıdır
            b.setCreatedAt(LocalDate.now());

            b.setImageUrl(imageUrl);
            b.setIsActive(true);

            b.setTags(resolveTags(dto.getTags()));

            return blogRepository.save(b).getId();

        } catch (RuntimeException ex) {
            fileStorageService.deleteIfExists(imageUrl);
            throw ex;
        }
    }

    @Override
    public Page<BlogAdminListDto> adminGetBlogs(int page, int size, String search, String tag, Boolean isActive) {
        var pageable = PageRequest.of(page, size);

        Page<Blog> p;

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasTag = tag != null && !tag.isBlank();

        if (hasTag) {
            if (isActive == null) p = blogRepository.findAllByTagSlug(tag.trim(), pageable);
            else p = blogRepository.findAllByTagSlugAndActive(tag.trim(), isActive, pageable);
        } else if (hasSearch) {
            if (isActive == null) p = blogRepository.searchAll(search.trim(), pageable);
            else p = blogRepository.searchAllByActive(search.trim(), isActive, pageable);
        } else {
            if (isActive == null) p = blogRepository.findAllByOrderByCreatedAtDesc(pageable);
            else p = blogRepository.findAllByIsActiveOrderByCreatedAtDesc(isActive, pageable);
        }

        return p.map(b -> {
            BlogAdminListDto d = new BlogAdminListDto();
            d.setId(b.getId());
            d.setTitle(b.getTitle());
            d.setAuthor(b.getAuthor());
            d.setCreatedAt(b.getCreatedAt());
            d.setIsActive(b.getIsActive());
            d.setImageUrl(b.getImageUrl());
            return d;
        });
    }

    @Override
    public BlogAdminUpdateDto adminGetEditForm(Long id) {
        var blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog tapılmadı"));

        BlogAdminUpdateDto dto = new BlogAdminUpdateDto();
        dto.setTitle(blog.getTitle());
        dto.setShortDescription(blog.getShortDescription());
        dto.setContent(blog.getContent());
        dto.setIsActive(Boolean.TRUE.equals(blog.getIsActive()));

        String tags = (blog.getTags() == null) ? ""
                : blog.getTags().stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsActive()))
                .map(t -> t.getName())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        dto.setTags(tags);

        return dto;
    }

    @Override
    public void adminUpdateBlog(Long id, BlogAdminUpdateDto dto, MultipartFile image) {
        var blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog tapılmadı"));

        String oldImage = blog.getImageUrl();
        String newImage = null;

        try {
            blog.setTitle(dto.getTitle() == null ? null : dto.getTitle().trim());
            blog.setShortDescription(dto.getShortDescription() == null ? null : dto.getShortDescription().trim());
            blog.setContent(dto.getContent() == null ? null : dto.getContent().trim());
            blog.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : blog.getIsActive());

            blog.setTags(resolveTags(dto.getTags()));

            if (image != null && !image.isEmpty()) {
                newImage = fileStorageService.storeBlogImage(image);
                blog.setImageUrl(newImage);
            }

            blogRepository.save(blog);

            if (newImage != null) {
                fileStorageService.deleteIfExists(oldImage);
            }

        } catch (RuntimeException ex) {
            fileStorageService.deleteIfExists(newImage);
            throw ex;
        }
    }

    @Override
    public void adminSetActive(Long id, boolean active) {
        var blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog tapılmadı"));
        blog.setIsActive(active);
        blogRepository.save(blog);
    }

    @Override
    public void adminDeleteBlog(Long id) {
        var blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog tapılmadı"));

        String img = blog.getImageUrl();
        blogRepository.delete(blog);
        fileStorageService.deleteIfExists(img);
    }

    @Override
    public void deleteMyBlog(String authorEmail, Long blogId) {

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog tapılmadı"));

        var me = userRepository.findByEmailIgnoreCase(authorEmail.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));

        if (blog.getAuthorId() == null || !blog.getAuthorId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu blogu silməyə icazən yoxdur");
        }

        String img = blog.getImageUrl();
        blogRepository.delete(blog);
        fileStorageService.deleteIfExists(img);
    }

    // ✅ AUTHOR NAME-li list dto
    private BlogListDto toListDto(Blog blog,
                                  Map<Long, com.example.demo.model.User> userByIdCache,
                                  Map<String, com.example.demo.model.User> userByEmailCache) {

        BlogListDto dto = new BlogListDto();
        dto.setId(blog.getId());
        dto.setTitle(blog.getTitle());
        dto.setShortDescription(blog.getShortDescription());
        dto.setImageUrl(blog.getImageUrl());
        dto.setAuthor(blog.getAuthor());
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setCommentCount(blogCommentRepository.countByBlog_IdAndIsActiveTrue(blog.getId()));

        com.example.demo.model.User u = null;

        if (blog.getAuthorId() != null) {
            u = userByIdCache.get(blog.getAuthorId());
            if (u == null) {
                u = userRepository.findById(blog.getAuthorId()).orElse(null);
                if (u != null) userByIdCache.put(blog.getAuthorId(), u);
            }
        } else if (blog.getAuthor() != null && !blog.getAuthor().isBlank()) {
            String key = blog.getAuthor().trim().toLowerCase();
            u = userByEmailCache.get(key);
            if (u == null) {
                u = userRepository.findByEmailIgnoreCase(key).orElse(null);
                if (u != null) userByEmailCache.put(key, u);
            }
        }

        if (u != null) {
            String name = (u.getFullName() != null && !u.getFullName().isBlank())
                    ? u.getFullName().trim()
                    : u.getEmail();
            dto.setAuthorName(name);
        } else {
            dto.setAuthorName(blog.getAuthor());
        }

        return dto;
    }

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