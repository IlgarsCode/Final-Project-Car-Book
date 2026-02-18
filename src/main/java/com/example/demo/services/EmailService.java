package com.example.demo.services;

import com.example.demo.dto.contact.ContactDto;

public interface EmailService {
    void sendContactMail(String name, String email, String subject, String message);
    void sendContactMail(ContactDto dto);

    // ✅ NEW: OTP mail
    void sendOtpMail(String toEmail, String subject, String text);
}
