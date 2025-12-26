package com.example.demo.services.impl;

import com.example.demo.model.ContactInfo;
import com.example.demo.repository.ContactInfoRepository;
import com.example.demo.services.ContactInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactInfoServiceImpl implements ContactInfoService {

    private final ContactInfoRepository repository;

    @Override
    public ContactInfo getContactInfo() {
        return repository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    ContactInfo info = new ContactInfo();
                    info.setAddress("Adres qeyd olunmayıb");
                    info.setPhone("+000000000");
                    info.setEmail("info@example.com");
                    return info;
                });
    }
}

