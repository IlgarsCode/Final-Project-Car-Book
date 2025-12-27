package com.example.demo.services.impl;

import com.example.demo.dto.contact.ContactDto;
import com.example.demo.model.ContactMessage;
import com.example.demo.repository.ContactRepository;
import com.example.demo.services.ContactMessageService;
import com.example.demo.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContactMessageServiceImpl implements ContactMessageService {

    private final ContactRepository contactRepository;
    private final EmailService mailService;

    @Override
    public void saveAndSend(ContactDto dto) {

        // 1️⃣ DB-yə yaz
        ContactMessage message = new ContactMessage();
        message.setName(dto.getName());
        message.setEmail(dto.getEmail());
        message.setSubject(dto.getSubject());
        message.setMessage(dto.getMessage());
        message.setCreatedAt(LocalDateTime.now());

        contactRepository.save(message);

        // 2️⃣ Mail göndər
        mailService.sendContactMail(dto);
    }

    @Override
    public void save(ContactMessage message) {

    }
}