package com.example.demo.services;

import com.example.demo.dto.contact.ContactInfoUpdateDto;
import com.example.demo.model.ContactInfo;

public interface ContactInfoService {

    ContactInfo getOrCreateSingleton();   // ADMIN üçün: heç vaxt null deyil

    ContactInfo getActiveForWeb();        // WEB üçün

    void update(ContactInfoUpdateDto dto);
}