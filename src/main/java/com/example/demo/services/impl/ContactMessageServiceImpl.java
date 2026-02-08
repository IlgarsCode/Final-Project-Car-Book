package com.example.demo.services.impl;

import com.example.demo.dto.contact.ContactDto;
import com.example.demo.model.ContactMessage;
import com.example.demo.repository.ContactMessageRepository;
import com.example.demo.services.ContactMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContactMessageServiceImpl implements ContactMessageService {

    private final ContactMessageRepository repository;

    @Override
    @Transactional
    public void saveMessage(ContactDto dto) {
        ContactMessage m = new ContactMessage();
        m.setName(dto.getName());
        m.setEmail(dto.getEmail());
        m.setSubject(dto.getSubject());
        m.setMessage(dto.getMessage());
        m.setCreatedAt(LocalDateTime.now());
        m.setRead(false);

        repository.save(m);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactMessage> getPage(int page, int size, Boolean unreadOnly) {
        PageRequest pr = PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        if (unreadOnly != null && unreadOnly) {
            return repository.findByIsReadOrderByCreatedAtDesc(false, pr);
        }
        return repository.findAllByOrderByCreatedAtDesc(pr);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactMessage getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mesaj tapılmadı: " + id));
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        ContactMessage m = getById(id);
        if (!m.isRead()) {
            m.setRead(true);
            repository.save(m);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread() {
        return repository.countByIsReadFalse();
    }
}
