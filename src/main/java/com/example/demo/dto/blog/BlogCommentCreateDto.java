package com.example.demo.dto.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogCommentCreateDto {

    @NotBlank(message = "Mesaj boş ola bilməz")
    @Size(max = 2000, message = "Mesaj çox uzundur")
    private String message;
}
