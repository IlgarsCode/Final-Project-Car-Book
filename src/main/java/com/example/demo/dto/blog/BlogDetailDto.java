package com.example.demo.dto.blog;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BlogDetailDto {

    private Long id;
    private String title;
    private String author;
    private LocalDate createdAt;

    private String imageUrl;

    private String content;
    private String authorName;
    private String authorAvatarUrl;
    private String authorBio;
    private String authorPhotoUrl;
    private List<BlogCommentDto> comments;
    private long commentCount;
    private List<TagDto> tags;
}
