package com.example.demo.dto.blog;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BlogCommentDto {
    private Long id;
    private String fullName;
    private String message;
    private LocalDateTime createdAt;
}
