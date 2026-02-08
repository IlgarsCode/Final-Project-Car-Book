package com.example.demo.dto.contact;

import lombok.Data;

@Data
public class ContactInfoUpdateDto {
    private String address;
    private String phone;
    private String email;
    private boolean active;
}