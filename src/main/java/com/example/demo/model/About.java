package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "about")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class About {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pageTitle;

    @Column(nullable = false)
    private String sectionTitle;

    @Column(length = 5000)
    private String description;

    private String imageUrl;

    private String bannerImageUrl;

    private boolean isActive;
}
