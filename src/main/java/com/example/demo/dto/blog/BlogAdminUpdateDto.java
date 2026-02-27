package com.example.demo.dto.blog;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogAdminUpdateDto {

    @NotBlank(message = "Title boş ola bilməz")
    private String title;

    @NotBlank(message = "Short description boş ola bilməz")
    private String shortDescription;

    @NotBlank(message = "Content boş ola bilməz")
    private String content;

    private String tags;

    private Boolean isActive;
}
