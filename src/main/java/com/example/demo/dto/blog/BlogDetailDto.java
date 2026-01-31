package com.example.demo.dto.blog;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BlogDetailDto {

    private Long id;
    private String title;
    private String author;
    private LocalDate createdAt;

    private String imageUrl;

    private String content;

    private String authorPhotoUrl;
    private String authorBio;
}
