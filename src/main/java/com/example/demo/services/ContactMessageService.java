package com.example.demo.services;

import com.example.demo.dto.contact.ContactDto;
import com.example.demo.model.ContactMessage;

public interface ContactMessageService {
    void saveAndSend(ContactDto dto);

    void save(ContactMessage message);
}
