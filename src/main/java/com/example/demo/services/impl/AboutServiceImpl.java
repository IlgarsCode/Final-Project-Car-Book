package com.example.demo.services.impl;

import com.example.demo.dto.about.AboutUpdateDto;
import com.example.demo.model.About;
import com.example.demo.repository.AboutRepository;
import com.example.demo.services.AboutService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AboutServiceImpl implements AboutService {

    private final AboutRepository aboutRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public About getAbout() {
        About about = aboutRepository.findTopByOrderByIdAsc();

        if (about == null) {
            about = new About();
            about.setPageTitle("About");
            about.setSectionTitle("About");
            about.setDescription("");
            about.setImageUrl(null);

            // 1 dənə aktiv About konsepti
            about.setActive(true);

            aboutRepository.save(about);
        }

        return about;
    }

    @Override
    public About getActiveAbout() {
        About active = aboutRepository.findByIsActiveTrue();
        if (active != null) return active;

        // fallback: heç olmasa bir record göstər
        return aboutRepository.findTopByOrderByIdAsc();
    }

    @Override
    public void update(AboutUpdateDto dto, MultipartFile image) {
        About about = getAbout();

        about.setPageTitle(dto.getPageTitle());
        about.setSectionTitle(dto.getSectionTitle());
        about.setDescription(dto.getDescription());

        String savedImage = saveAboutPhoto(image);
        if (savedImage != null) {
            about.setImageUrl(savedImage);
        } else {
            // şəkil seçilməyibsə əvvəlki qalsın
            if (StringUtils.hasText(dto.getImageUrl())) {
                about.setImageUrl(dto.getImageUrl());
            }
        }

        // Bu səhifə 1 dənədir və aktiv qalmalıdır
        about.setActive(true);

        aboutRepository.save(about);
    }

    private String saveAboutPhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        try {
            String ext = Optional.ofNullable(file.getOriginalFilename())
                    .filter(n -> n.contains("."))
                    .map(n -> n.substring(n.lastIndexOf(".")))
                    .orElse("");

            String filename = UUID.randomUUID() + ext;

            // app.upload.dir/about/...
            Path dir = Paths.get(uploadDir, "about");
            Files.createDirectories(dir);

            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());

            // DB-də saxlanacaq URL
            return "/uploads/about/" + filename;

        } catch (Exception e) {
            throw new RuntimeException("About şəkil upload xətası: " + e.getMessage(), e);
        }
    }
}
