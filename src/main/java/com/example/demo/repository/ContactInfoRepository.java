package com.example.demo.repository;

import com.example.demo.model.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactInfoRepository
        extends JpaRepository<ContactInfo, Long> {

    Optional<ContactInfo> findByIsActiveTrue();
}
