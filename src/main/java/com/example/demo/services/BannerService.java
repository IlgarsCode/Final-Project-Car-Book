package com.example.demo.services;


import com.example.demo.enums.BannerType;
import com.example.demo.model.Banner;

public interface BannerService {

    Banner getActiveBanner(BannerType bannerType);

    Object getHomeBanner();

    Banner getAboutBanner();

    Banner getDefaultBanner();
}
