package com.example.demo.services;

import com.example.demo.dto.contact.ContactInfoUpdateDto;
import com.example.demo.model.ContactInfo;

public interface ContactInfoService {

    ContactInfo getOrCreateSingleton();

    ContactInfo getActiveForWeb();

    void update(ContactInfoUpdateDto dto);
}