package com.example.demo.services.storage.impl;

import com.example.demo.services.storage.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_EXT = Set.of("jpg","jpeg","png","webp");
    private static final long MAX_AVATAR_BYTES = 10 * 1024 * 1024 * 1024 * 1024; // 2MB

    @Override
    public String storeCarImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(original);
        ext = (ext == null) ? "" : ext.toLowerCase();

        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("Yalnız şəkil formatları: jpg, jpeg, png, webp");
        }

        String filename = UUID.randomUUID() + "." + ext;

        Path carsDir = Paths.get(uploadDir, "cars");
        try {
            Files.createDirectories(carsDir);

            Path target = carsDir.resolve(filename);
            file.transferTo(target);

            return "/uploads/cars/" + filename;

        } catch (IOException e) {
            log.error("Car şəkli saxlanılmadı", e);
            throw new RuntimeException("Şəkil saxlanılmadı");
        }
    }

    @Override
    public String storeBlogImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(original);
        ext = (ext == null) ? "" : ext.toLowerCase();

        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("Yalnız şəkil formatları: jpg, jpeg, png, webp");
        }

        String filename = UUID.randomUUID() + "." + ext;

        Path dir = Paths.get(uploadDir, "blogs");
        try {
            Files.createDirectories(dir);

            Path target = dir.resolve(filename);
            file.transferTo(target);

            return "/uploads/blogs/" + filename;

        } catch (IOException e) {
            log.error("Blog şəkli saxlanılmadı", e);
            throw new RuntimeException("Şəkil saxlanılmadı");
        }
    }

    // ✅ yeni
    @Override
    public String storeUserAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        if (file.getSize() > MAX_AVATAR_BYTES) {
            throw new IllegalArgumentException("Avatar maksimum 2MB ola bilər");
        }

        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(original);
        ext = (ext == null) ? "" : ext.toLowerCase();

        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("Yalnız şəkil formatları: jpg, jpeg, png, webp");
        }

        String filename = UUID.randomUUID() + "." + ext;

        Path dir = Paths.get(uploadDir, "avatars");
        try {
            Files.createDirectories(dir);

            Path target = dir.resolve(filename);
            file.transferTo(target);

            return "/uploads/avatars/" + filename;

        } catch (IOException e) {
            log.error("Avatar saxlanılmadı", e);
            throw new RuntimeException("Avatar saxlanılmadı");
        }
    }

    @Override
    public void deleteIfExists(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        if (!relativePath.startsWith("/uploads/")) return;

        String cleaned = relativePath.replace("/uploads/", "");
        Path target = Paths.get(uploadDir, cleaned);

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("Şəkil silinmədi: {}", target, e);
        }
    }
}
