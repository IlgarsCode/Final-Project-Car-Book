package com.example.demo.dto.contact;

import lombok.Data;

@Data
public class ContactDto {
    private String name;
    private String email;
    private String subject;
    private String message;

    private String address;
    private String phone;

    // bannerPhotoUrl artıq lazım deyil, banneri BannerService-dən alırsan.
    // Amma saxlayacaqsansa String olsun:
    private String bannerPhotoUrl;
}