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

    private static final String TO_EMAIL = "ilgartest77@gmail.com";

    @Override
    public void sendContactMail(String name, String email, String subject, String message) {
        ContactDto dto = new ContactDto();
        dto.setName(name);
        dto.setEmail(email);
        dto.setSubject(subject);
        dto.setMessage(message);
        sendContactMail(dto);
    }

    @Override
    public void sendContactMail(ContactDto dto) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setTo(TO_EMAIL);
            mailMessage.setFrom(TO_EMAIL);
            mailMessage.setReplyTo(dto.getEmail());
            mailMessage.setSubject("📩 Contact Form: " + dto.getSubject());

            mailMessage.setText(
                    "Name: " + dto.getName() + "\n" +
                            "Email: " + dto.getEmail() + "\n\n" +
                            "Message:\n" +
                            dto.getMessage()
            );

            mailSender.send(mailMessage);

            log.info("✅ Contact mail Gmail-ə göndərildi");

        } catch (Exception e) {
            log.error("❌ Mail göndərilmədi", e);
            throw new RuntimeException("Mail göndərilmədi");
        }
    }
}
