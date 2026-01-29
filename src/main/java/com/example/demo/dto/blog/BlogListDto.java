package com.example.demo.dto.blog;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BlogListDto {
    private Long id;
    private String title;
    private String shortDescription;
    private String imageUrl;
    private String author;
    private LocalDate createdAt;
}
