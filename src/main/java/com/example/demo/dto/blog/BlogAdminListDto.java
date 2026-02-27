package com.example.demo.dto.blog;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BlogAdminListDto {
    private Long id;
    private String title;
    private String author;
    private LocalDate createdAt;
    private Boolean isActive;
    private String imageUrl;
}
