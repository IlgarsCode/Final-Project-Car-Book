package com.example.demo.services.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeCarImage(MultipartFile file);
    String storeBlogImage(MultipartFile file);

    // ✅ yeni
    String storeUserAvatar(MultipartFile file);

    void deleteIfExists(String relativePath);
}
