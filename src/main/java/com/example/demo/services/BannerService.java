package com.example.demo.services;

import com.example.demo.dto.banner.*;
import com.example.demo.dto.enums.BannerType;
import com.example.demo.model.Banner;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BannerService {

    Banner getBanner(BannerType bannerType);

    List<BannerListDto> getAll();

    BannerUpdateDto getByIdForUpdate(Long id);

    void create(BannerCreateDto dto, MultipartFile photo);

    void update(BannerUpdateDto dto, MultipartFile photo);

    void delete(BannerDeleteDto dto);
}
