package com.example.demo.model;

import com.example.demo.enums.BannerType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name= "banner")
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String photoUrl;
    private String videoUrl;
    private String buttonText;
    private String buttonUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BannerType bannerType;

    @Column(nullable = false)
    private boolean isActive;
}
