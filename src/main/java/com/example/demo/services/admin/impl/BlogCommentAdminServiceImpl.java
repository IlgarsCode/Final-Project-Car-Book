package com.example.demo.services.admin.impl;

import com.example.demo.dto.blog.BlogCommentAdminRowDto;
import com.example.demo.repository.BlogCommentRepository;
import com.example.demo.services.admin.BlogCommentAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BlogCommentAdminServiceImpl implements BlogCommentAdminService {

    private final BlogCommentRepository blogCommentRepository;

    @Override
    public Page<BlogCommentAdminRowDto> getComments(int page, int size, String q, Long blogId, Boolean active, boolean rootOnly) {
        int safeSize = Math.min(Math.max(size, 5), 100);
        int safePage = Math.max(page, 0);

        var pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        var p = blogCommentRepository.adminSearch(
                (q == null ? null : q.trim()),
                blogId,
                active,
                rootOnly,
                pageable
        );

        return p.map(c -> {
            BlogCommentAdminRowDto d = new BlogCommentAdminRowDto();
            d.setId(c.getId());

            d.setBlogId(c.getBlog().getId());
            d.setBlogTitle(c.getBlog().getTitle());

            d.setParentId(c.getParent() != null ? c.getParent().getId() : null);

            d.setFullName(c.getFullName());
            d.setEmail(c.getEmail());

            String msg = c.getMessage() == null ? "" : c.getMessage().trim();
            d.setMessagePreview(msg.length() > 140 ? msg.substring(0, 140) + "..." : msg);

            d.setCreatedAt(c.getCreatedAt());
            d.setIsActive(c.getIsActive());

            return d;
        });
    }

    @Override
    public void setActive(Long id, boolean active) {
        var c = blogCommentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment tapılmadı"));
        c.setIsActive(active);
        blogCommentRepository.save(c);
    }

    @Override
    public void delete(Long id) {
        setActive(id, false);
    }

    @Override
    public void hardDelete(Long id) {
        var c = blogCommentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment tapılmadı"));
        blogCommentRepository.delete(c);
    }
}
