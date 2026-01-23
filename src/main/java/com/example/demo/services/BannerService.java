package com.example.demo.services;


import com.example.demo.dto.enums.BannerType;
import com.example.demo.model.Banner;

public interface BannerService {

    Banner getBanner(BannerType bannerType);

}

