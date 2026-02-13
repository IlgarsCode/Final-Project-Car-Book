package com.example.demo.dto.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogReplyCreateDto {

    @NotNull(message = "Reply üçün parent seçilməlidir")
    private Long parentId;

    @NotBlank(message = "Mesaj boş ola bilməz")
    @Size(max = 2000, message = "Mesaj çox uzundur")
    private String message;
}
