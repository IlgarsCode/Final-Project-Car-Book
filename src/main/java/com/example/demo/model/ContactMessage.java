package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String subject;

    @Column(length = 5000)
    private String message;

    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isRead = false;
}
