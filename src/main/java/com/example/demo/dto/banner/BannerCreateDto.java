package com.example.demo.dto.banner;

import com.example.demo.dto.enums.BannerType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BannerCreateDto {
    private String title;
    private String description;
    private String photoUrl;

    private String videoUrl;

    private String buttonText;
    private String buttonUrl;

    @NotNull
    private BannerType bannerType;

    private boolean active = true;
}
