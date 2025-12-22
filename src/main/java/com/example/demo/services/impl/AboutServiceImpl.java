package com.example.demo.services.impl;

import com.example.demo.model.About;
import com.example.demo.repository.AboutRepository;
import com.example.demo.services.AboutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AboutServiceImpl implements AboutService {

    private final AboutRepository aboutRepository;

    @Override
    public About getActiveAbout() {
        return aboutRepository.findByIsActiveTrue();
    }
}
