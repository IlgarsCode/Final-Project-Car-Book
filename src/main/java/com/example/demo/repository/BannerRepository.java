package com.example.demo.repository;

import com.example.demo.dto.enums.BannerType;
import com.example.demo.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    Banner findByBannerTypeAndIsActiveTrue(BannerType bannerType);

    Optional<Banner> findFirstByBannerTypeAndIsActiveTrueOrderByIdDesc(BannerType bannerType);

    List<Banner> findAllByOrderByIdDesc();

    List<Banner> findAllByBannerTypeAndIsActiveTrue(BannerType bannerType);
}
