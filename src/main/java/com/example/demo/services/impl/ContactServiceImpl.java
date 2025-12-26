package com.example.demo.services.impl;

import com.example.demo.dto.contact.ContactDto;
import com.example.demo.model.ContactInfo;
import com.example.demo.model.ContactMessage;
import com.example.demo.repository.ContactInfoRepository;
import com.example.demo.repository.ContactRepository;
import com.example.demo.services.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactInfoRepository contactInfoRepository;

    // 📌 CONTACT PAGE INFO
    @Override
    public ContactDto getContactInfo() {

        ContactInfo entity = contactInfoRepository.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("Contact info tapılmadı"));

        ContactDto dto = new ContactDto();
        dto.setAddress(entity.getAddress());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setBannerPhotoUrl(entity.getBannerPhotoUrl());

        return dto;
    }

    // 📌 CONTACT FORM MESSAGE
    @Override
    public void saveMessage(ContactDto dto) {

        ContactMessage message = new ContactMessage();
        message.setName(dto.getName());
        message.setEmail(dto.getEmail());
        message.setSubject(dto.getSubject());
        message.setMessage(dto.getMessage());
        message.setCreatedAt(LocalDateTime.now());

        contactRepository.save(message);
    }
}
