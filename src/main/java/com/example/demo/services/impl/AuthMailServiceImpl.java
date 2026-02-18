package com.example.demo.services.impl;

import com.example.demo.services.AuthMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthMailServiceImpl implements AuthMailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendPasswordResetOtp(String toEmail, String otpCode, int expiresMinutes) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            // from boş qalsa da olur, amma gmail username yazmaq daha stabil olur
            // msg.setFrom("ilgartest77@gmail.com");

            msg.setSubject("Password Reset OTP");
            msg.setText(
                    "Salam!\n\n" +
                            "Şifrəni yeniləmək üçün OTP kodunuz: " + otpCode + "\n" +
                            "Bu kod " + expiresMinutes + " dəqiqə aktivdir.\n\n" +
                            "Əgər bunu siz etməmisinizsə, bu mesajı nəzərə almayın."
            );

            mailSender.send(msg);
            log.info("✅ Reset OTP mail göndərildi -> {}", toEmail);

        } catch (Exception e) {
            log.error("❌ OTP mail göndərilmədi", e);
            throw new RuntimeException("OTP mail göndərilmədi");
        }
    }
}
