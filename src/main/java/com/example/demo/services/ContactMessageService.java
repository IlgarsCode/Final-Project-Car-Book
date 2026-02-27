package com.example.demo.services;

import com.example.demo.dto.contact.ContactDto;
import com.example.demo.model.ContactMessage;
import org.springframework.data.domain.Page;

public interface ContactMessageService {

    void saveMessage(ContactDto dto);

    Page<ContactMessage> getPage(int page, int size, Boolean unreadOnly);

    ContactMessage getById(Long id);

    void markAsRead(Long id);

    void delete(Long id);

    long countUnread();
}
