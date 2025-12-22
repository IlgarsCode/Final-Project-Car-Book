package com.example.demo.services.impl;


import com.example.demo.enums.BannerType;
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
    public Banner getActiveBanner(BannerType bannerType) {
        return null;
    }

    @Override
    public Banner getHomeBanner() {
        return bannerRepository.findByBannerTypeAndIsActiveTrue(BannerType.HOME);
    }

    @Override
    public Banner getAboutBanner() {
        return null;
    }

    @Override
    public Banner getDefaultBanner() {
        return bannerRepository.findByBannerTypeAndIsActiveTrue(BannerType.DEFAULT);
    }
}
