package com.example.demo.dto.banner;

import com.example.demo.enums.BannerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerDto {

    private Long id;

    private String title;
    private String description;

    private String photoUrl;
    private String videoUrl;

    private String buttonText;
    private String buttonUrl;

    private BannerType bannerType;
}
