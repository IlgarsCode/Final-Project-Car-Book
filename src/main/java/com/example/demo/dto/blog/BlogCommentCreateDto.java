package com.example.demo.dto.blog;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogCommentCreateDto {

    @NotBlank(message = "Ad boş ola bilməz")
    @Size(max = 80, message = "Ad çox uzundur")
    private String fullName;

    @NotBlank(message = "Email boş ola bilməz")
    @Email(message = "Email formatı yanlışdır")
    @Size(max = 120, message = "Email çox uzundur")
    private String email;

    @Size(max = 200, message = "Website çox uzundur")
    private String website;

    @NotBlank(message = "Mesaj boş ola bilməz")
    @Size(max = 2000, message = "Mesaj çox uzundur")
    private String message;
}
