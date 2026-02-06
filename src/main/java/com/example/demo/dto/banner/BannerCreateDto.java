package com.example.demo.dto.banner;

import com.example.demo.dto.enums.BannerType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BannerCreateDto {
    private String title;
    private String description;

    // file upload ilə gələcək, amma fallback üçün saxlayırıq
    private String photoUrl;

    // yalnız HOME üçün lazım ola bilər (sən dedin frontend indexdə)
    private String videoUrl;

    private String buttonText;
    private String buttonUrl;

    @NotNull
    private BannerType bannerType;

    // default: true qoymaq rahatdır (istəsən false də edə bilərsən)
    private boolean active = true;
}
