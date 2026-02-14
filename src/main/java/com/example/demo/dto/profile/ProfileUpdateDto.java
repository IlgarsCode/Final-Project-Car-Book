package com.example.demo.dto.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 3, max = 60)
    private String fullName;

    // istəsən @Pattern ilə daha sərt edə bilərsən
    @Size(max = 40)
    private String phone;

    @Size(max = 300)
    private String bio;
}
