package com.example.demo.services;

import com.example.demo.dto.about.AboutUpdateDto;
import com.example.demo.model.About;
import org.springframework.web.multipart.MultipartFile;

public interface AboutService {
    About getAbout();
    About getActiveAbout();
    void update(AboutUpdateDto dto, MultipartFile image);
}