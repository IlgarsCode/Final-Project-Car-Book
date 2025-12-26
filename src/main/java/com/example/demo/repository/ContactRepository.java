package com.example.demo.repository;

import com.example.demo.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ContactRepository extends JpaRepository<ContactMessage, Long> {

    Optional<ContactMessage> findFirstByOrderByIdAsc();
}


