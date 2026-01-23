package com.example.demo.services.impl;

import com.example.demo.dto.enums.BannerType;
import com.example.demo.model.Banner;
import com.example.demo.repository.BannerRepository;
import com.example.demo.services.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    @Override
    public Banner getBanner(BannerType bannerType) {

        Banner banner = bannerRepository
                .findFirstByBannerTypeAndIsActiveTrueOrderByIdDesc(bannerType)
                .orElse(null);

        if (banner == null) {
            banner = bannerRepository
                    .findFirstByBannerTypeAndIsActiveTrueOrderByIdDesc(BannerType.DEFAULT)
                    .orElse(null);
        }

        return banner;
    }
}
