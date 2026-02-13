package com.example.demo.services.admin;

import com.example.demo.dto.blog.BlogCommentAdminRowDto;
import org.springframework.data.domain.Page;

public interface BlogCommentAdminService {
    Page<BlogCommentAdminRowDto> getComments(int page, int size, String q, Long blogId, Boolean active, boolean rootOnly);

    void setActive(Long id, boolean active);
    void delete(Long id);      // soft
    void hardDelete(Long id);  // hard
}
