package com.example.demo.dto.blog;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogCommentCreateDto {

    @NotBlank(message = "Ad boş ola bilməz")
    private String fullName;

    @NotBlank(message = "Email boş ola bilməz")
    @Email(message = "Email formatı yanlışdır")
    private String email;

    private String website;

    @NotBlank(message = "Mesaj boş ola bilməz")
    private String message;
}
