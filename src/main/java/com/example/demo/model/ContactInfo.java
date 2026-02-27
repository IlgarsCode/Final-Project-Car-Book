package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contact_info")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="page_title")
    private String pageTitle;

    private String address;
    private String phone;
    private String email;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}