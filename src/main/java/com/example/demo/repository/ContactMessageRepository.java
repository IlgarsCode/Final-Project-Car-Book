package com.example.demo.repository;

import com.example.demo.model.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    long countByIsReadFalse();

    Page<ContactMessage> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<ContactMessage> findByIsReadOrderByCreatedAtDesc(boolean isRead, Pageable pageable);
}
