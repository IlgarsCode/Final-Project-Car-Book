package com.example.demo.services.impl;

import com.example.demo.dto.contact.ContactDto;
import com.example.demo.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendContactMail(String name, String email, String subject, String message) {

    }

    @Override
    public void sendContactMail(ContactDto dto) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setTo("winnifred.mitchell26@ethereal.email");
            mailMessage.setFrom("winnifred.mitchell26@ethereal.email");
            mailMessage.setSubject("Contact Form: " + dto.getSubject());

            mailMessage.setText(
                    "Name: " + dto.getName() + "\n" +
                            "Email: " + dto.getEmail() + "\n\n" +
                            "Message:\n" + dto.getMessage()
            );

            mailSender.send(mailMessage);

            log.info("✅ Mail uğurla göndərildi");

        } catch (Exception e) {
            log.error("❌ Mail göndərilərkən xəta baş verdi", e);
        }
    }
}
