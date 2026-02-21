package com.example.demo.services.storage.impl;

import com.example.demo.services.storage.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");

    // ✅ Normal limitlər
    private static final long MAX_AVATAR_BYTES = 2L * 1024 * 1024;       // 2 MB
    private static final long MAX_IMAGE_BYTES  = 5L * 1024 * 1024;       // 5 MB (car/blog üçün)

    @Override
    public String storeCarImage(MultipartFile file) {
        return storeImage(file, "cars", "/uploads/cars/", MAX_IMAGE_BYTES, "Car şəkli saxlanılmadı");
    }

    @Override
    public String storeBlogImage(MultipartFile file) {
        return storeImage(file, "blogs", "/uploads/blogs/", MAX_IMAGE_BYTES, "Blog şəkli saxlanılmadı");
    }

    @Override
    public String storeUserAvatar(MultipartFile file) {
        return storeImage(file, "avatars", "/uploads/avatars/", MAX_AVATAR_BYTES, "Avatar saxlanılmadı");
    }

    private String storeImage(MultipartFile file,
                              String folderName,
                              String publicPrefix,
                              long maxBytes,
                              String logMsg) {

        if (file == null || file.isEmpty()) return null;

        if (file.getSize() > maxBytes) {
            long maxMb = Math.max(1, maxBytes / (1024 * 1024));
            throw new IllegalArgumentException("Fayl maksimum " + maxMb + "MB ola bilər");
        }

        String original = file.getOriginalFilename() == null ? "" : StringUtils.cleanPath(file.getOriginalFilename());
        String ext = StringUtils.getFilenameExtension(original);
        ext = (ext == null) ? "" : ext.toLowerCase();

        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("Yalnız şəkil formatları: jpg, jpeg, png, webp");
        }

        String filename = UUID.randomUUID() + "." + ext;

        Path dir = Paths.get(uploadDir, folderName);
        try {
            Files.createDirectories(dir);

            Path target = dir.resolve(filename);
            file.transferTo(target);

            return publicPrefix + filename;

        } catch (IOException e) {
            log.error(logMsg, e);
            throw new RuntimeException(logMsg);
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