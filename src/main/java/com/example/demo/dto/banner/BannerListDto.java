package com.example.demo.dto.banner;

import com.example.demo.dto.enums.BannerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerListDto {
    private Long id;
    private String title;
    private String photoUrl;
    private boolean isActive;
    private BannerType bannerType;
}
