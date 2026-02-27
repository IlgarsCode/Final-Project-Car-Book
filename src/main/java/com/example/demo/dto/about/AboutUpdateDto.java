package com.example.demo.dto.about;

import lombok.Data;

@Data
public class AboutUpdateDto {
    private String pageTitle;
    private String sectionTitle;
    private String description;
    private String imageUrl;
}
