package com.example.demo.dto.blog;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BlogCommentDto {
    private Long id;
    private String fullName;
    private String message;
    private LocalDateTime createdAt;

    // ✅ reply-lar
    private List<BlogCommentDto> replies;
}
