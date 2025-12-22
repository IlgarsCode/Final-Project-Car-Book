package com.example.demo.dto.about;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AboutDto {
    private String pageTitle;

    private String sectionTitle;

    private String description;

    private String imageUrl;
}
