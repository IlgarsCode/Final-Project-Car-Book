package com.example.demo.dto.blog;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BlogCommentAdminRowDto {
    private Long id;

    private Long blogId;
    private String blogTitle;

    private Long parentId;
    private String fullName;
    private String email;

    private String messagePreview;

    private LocalDateTime createdAt;

    private Boolean isActive;
}
