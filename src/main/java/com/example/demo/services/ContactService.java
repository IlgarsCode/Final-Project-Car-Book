package com.example.demo.services;

import com.example.demo.dto.contact.ContactDto;
import com.example.demo.dto.contact.ContactMessageCreateDto;

public interface ContactService {
    void saveMessage(ContactDto contactDto);
}