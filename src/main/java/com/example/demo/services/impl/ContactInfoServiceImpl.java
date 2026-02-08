package com.example.demo.services.impl;

import com.example.demo.dto.contact.ContactInfoUpdateDto;
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
    public ContactInfo getOrCreateSingleton() {
        return repository.findTopByOrderByIdAsc()
                .orElseGet(() -> {
                    ContactInfo info = new ContactInfo();
                    info.setPageTitle("Contact");
                    info.setAddress("Adres qeyd olunmayıb");
                    info.setPhone("+000000000");
                    info.setEmail("info@example.com");
                    info.setActive(true);
                    return repository.save(info);
                });
    }

    @Override
    public ContactInfo getActiveForWeb() {
        return repository.findFirstByActiveTrueOrderByIdAsc()
                .orElseGet(this::getOrCreateSingleton);
    }

    @Override
    public void update(ContactInfoUpdateDto dto) {
        ContactInfo info = getOrCreateSingleton();
        info.setAddress(dto.getAddress());
        info.setPhone(dto.getPhone());
        info.setEmail(dto.getEmail());
        info.setActive(dto.isActive());
        repository.save(info);
    }
}
