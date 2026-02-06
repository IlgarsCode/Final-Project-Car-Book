package com.example.demo.services.impl;

import com.example.demo.dto.banner.BannerCreateDto;
import com.example.demo.dto.banner.BannerDeleteDto;
import com.example.demo.dto.banner.BannerListDto;
import com.example.demo.dto.banner.BannerUpdateDto;
import com.example.demo.dto.enums.BannerType;
import com.example.demo.model.Banner;
import com.example.demo.repository.BannerRepository;
import com.example.demo.services.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    @Value("${app.upload.dir:D:/Course Final Project/carbook/uploads}")
    private String uploadDir;

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

    @Override
    public List<BannerListDto> getAll() {
        return bannerRepository.findAllByOrderByIdDesc()
                .stream()
                .map(b -> new BannerListDto(
                        b.getId(),
                        b.getTitle(),
                        b.getPhotoUrl(),
                        b.isActive(),
                        b.getBannerType()
                ))
                .toList();
    }

    @Override
    public BannerUpdateDto getByIdForUpdate(Long id) {
        Banner b = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Banner tapılmadı: " + id));

        BannerUpdateDto dto = new BannerUpdateDto();
        dto.setId(b.getId());
        dto.setTitle(b.getTitle());
        dto.setDescription(b.getDescription());
        dto.setPhotoUrl(b.getPhotoUrl());
        dto.setVideoUrl(b.getVideoUrl());
        dto.setButtonText(b.getButtonText());
        dto.setButtonUrl(b.getButtonUrl());
        dto.setBannerType(b.getBannerType());
        dto.setActive(b.isActive());
        return dto;
    }

    @Override
    public void create(BannerCreateDto dto, MultipartFile photo) {
        Banner b = new Banner();
        b.setTitle(dto.getTitle());
        b.setDescription(dto.getDescription());
        b.setVideoUrl(dto.getVideoUrl());
        b.setButtonText(dto.getButtonText());
        b.setButtonUrl(dto.getButtonUrl());
        b.setBannerType(dto.getBannerType());
        b.setActive(dto.isActive());

        String savedPhotoUrl = saveBannerPhoto(photo);
        if (savedPhotoUrl != null) b.setPhotoUrl(savedPhotoUrl);
        else b.setPhotoUrl(dto.getPhotoUrl());

        // ❌ BUNU SİLİRİK (artıq digərlərini passive etməsin)
        // if (b.isActive()) {
        //     deactivateOthers(dto.getBannerType(), null);
        // }

        bannerRepository.save(b);
    }

    @Override
    public void update(BannerUpdateDto dto, MultipartFile photo) {
        Banner b = bannerRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Banner tapılmadı: " + dto.getId()));

        b.setTitle(dto.getTitle());
        b.setDescription(dto.getDescription());
        b.setVideoUrl(dto.getVideoUrl());
        b.setButtonText(dto.getButtonText());
        b.setButtonUrl(dto.getButtonUrl());
        b.setBannerType(dto.getBannerType());
        b.setActive(dto.isActive());

        String savedPhotoUrl = saveBannerPhoto(photo);
        if (savedPhotoUrl != null) b.setPhotoUrl(savedPhotoUrl);
        else if (org.springframework.util.StringUtils.hasText(dto.getPhotoUrl())) b.setPhotoUrl(dto.getPhotoUrl());

        // ❌ BUNU SİLİRİK (artıq digərlərini passive etməsin)
        // if (b.isActive()) {
        //     deactivateOthers(b.getBannerType(), b.getId());
        // }

        bannerRepository.save(b);
    }

    @Override
    public void delete(BannerDeleteDto dto) {
        Banner b = bannerRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Banner tapılmadı: " + dto.getId()));

        if (dto.isHardDelete()) {
            bannerRepository.delete(b);
        } else {
            // soft delete: sadəcə aktivliyi söndür
            b.setActive(false);
            bannerRepository.save(b);
        }
    }

    private void deactivateOthers(BannerType type, Long excludeId) {
        List<Banner> actives = bannerRepository.findAllByBannerTypeAndIsActiveTrue(type);

        for (Banner x : actives) {
            if (excludeId != null && excludeId.equals(x.getId())) continue;
            x.setActive(false);
        }

        bannerRepository.saveAll(actives);
    }

    /**
     * ƏN VACİB HİSSƏ:
     * Faylı diskə yazır:
     * {uploadDir}/banners/{uuid}.{ext}
     * DB-ə URL saxlayır:
     * /uploads/banners/{uuid}.{ext}
     */
    private String saveBannerPhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        try {
            String ext = Optional.ofNullable(file.getOriginalFilename())
                    .filter(n -> n.contains("."))
                    .map(n -> n.substring(n.lastIndexOf(".")))
                    .orElse("");

            String filename = UUID.randomUUID() + ext;

            Path dir = Paths.get(uploadDir, "banners");
            Files.createDirectories(dir);

            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());

            return "/uploads/banners/" + filename;

        } catch (Exception e) {
            throw new RuntimeException("File upload xətası: " + e.getMessage(), e);
        }
    }
}
