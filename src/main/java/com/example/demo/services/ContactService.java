package com.example.demo.services;

import com.example.demo.dto.contact.ContactDto;

public interface ContactService {
    void saveMessage(ContactDto contactDto);
    ContactDto getContactInfo();
}

