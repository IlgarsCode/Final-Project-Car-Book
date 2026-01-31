package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "blogs")
@Getter
@Setter
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    private String author;

    private String authorPhotoUrl;

    @Column(columnDefinition = "TEXT")
    private String authorBio;

    private LocalDate createdAt;

    private Boolean isActive;
}
