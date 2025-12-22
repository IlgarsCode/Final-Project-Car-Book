package com.example.demo.repository;

import com.example.demo.enums.BannerType;
import com.example.demo.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    Banner findByBannerTypeAndIsActiveTrue(BannerType bannerType);
}
