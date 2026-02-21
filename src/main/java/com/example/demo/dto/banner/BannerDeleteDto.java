package com.example.demo.dto.banner;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BannerDeleteDto {
    @NotNull
    private Long id;

    private boolean hardDelete = false;
}
