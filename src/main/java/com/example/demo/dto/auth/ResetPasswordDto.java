package com.example.demo.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDto {

    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 6, max = 64)
    private String newPassword;

    @NotBlank
    @Size(min = 6, max = 64)
    private String confirmNewPassword;
}
