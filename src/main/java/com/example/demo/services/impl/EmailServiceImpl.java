package com.example.demo.services.impl;

import com.example.demo.dto.contact.ContactDto;
import com.example.demo.model.*;
import com.example.demo.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name:CarBook}")
    private String fromName;

    @Value("${app.mail.admin}")
    private String adminEmail;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // ---------------- CONTACT ----------------
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
            mailMessage.setTo(adminEmail);

            // SimpleMailMessage’de personal name bazen sorun çıkarır; ama çoğu zaman çalışır.
            mailMessage.setFrom(fromName + " <" + fromEmail + ">");

            mailMessage.setReplyTo(dto.getEmail());
            mailMessage.setSubject("📩 Contact Form: " + dto.getSubject());
            mailMessage.setText(
                    "Name: " + dto.getName() + "\n" +
                            "Email: " + dto.getEmail() + "\n\n" +
                            "Message:\n" + dto.getMessage()
            );

            mailSender.send(mailMessage);
            log.info("✅ Contact mail sent -> {}", adminEmail);
        } catch (Exception e) {
            log.error("❌ Contact mail FAILED", e);
            throw new RuntimeException("Mail göndərilmədi");
        }
    }

    // ---------------- OTP ----------------
    @Override
    public void sendOtpMail(String toEmail, String subject, String text) {
        try {
            // OTP’yi de MIME ile atmak daha stabil (From name garantili)
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mime, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name()
            );
            helper.setTo(toEmail);
            helper.setFrom(fromEmail, fromName);
            helper.setSubject(subject);
            helper.setText(text, false);

            mailSender.send(mime);
            log.info("✅ OTP mail sent -> {}", toEmail);
        } catch (Exception e) {
            log.error("❌ OTP mail FAILED -> {}", toEmail, e);
            throw new RuntimeException("OTP mail göndərilmədi");
        }
    }

    // ---------------- ORDER CREATED (PENDING) ----------------
    @Override
    public void sendOrderCreatedPending(Order order) {
        String to = order.getUser().getEmail();
        String subject = "🧾 Order #" + order.getUserOrderNo() + " yaradıldı — ödənişi tamamla";

        String payNowUrl = baseUrl + "/payment/start/" + order.getId();
        String orderUrl = baseUrl + "/order/" + order.getId();

        Context ctx = new Context();
        ctx.setVariable("order", order);
        ctx.setVariable("payNowUrl", payNowUrl);
        ctx.setVariable("orderUrl", orderUrl);
        ctx.setVariable("pickupDate", order.getPickupDate() != null ? order.getPickupDate().format(DATE_FMT) : "-");
        ctx.setVariable("dropoffDate", order.getDropoffDate() != null ? order.getDropoffDate().format(DATE_FMT) : "-");

        sendHtml(to, subject, "mail/order-created", ctx);
    }

    // ---------------- PAYMENT SUCCEEDED ----------------
    @Override
    public void sendPaymentSucceeded(Payment payment) {
        Order order = payment.getOrder();
        String to = order.getUser().getEmail();
        String subject = "✅ Ödəniş qəbul olundu — Order #" + order.getUserOrderNo();

        String orderUrl = baseUrl + "/order/" + order.getId();

        Context ctx = new Context();
        ctx.setVariable("payment", payment);
        ctx.setVariable("order", order);
        ctx.setVariable("orderUrl", orderUrl);

        sendHtml(to, subject, "mail/payment-succeeded", ctx);
    }

    // ---------------- PAYMENT FAILED ----------------
    @Override
    public void sendPaymentFailed(Payment payment) {
        Order order = payment.getOrder();
        String to = order.getUser().getEmail();
        String subject = "❌ Ödəniş alınmadı — Order #" + order.getUserOrderNo();

        String retryUrl = baseUrl + "/payment/start/" + order.getId();
        String orderUrl = baseUrl + "/order/" + order.getId();

        Context ctx = new Context();
        ctx.setVariable("payment", payment);
        ctx.setVariable("order", order);
        ctx.setVariable("retryUrl", retryUrl);
        ctx.setVariable("orderUrl", orderUrl);

        sendHtml(to, subject, "mail/payment-failed", ctx);
    }

    // ---------------- ADMIN STATUS CHANGED ----------------
    @Override
    public void sendOrderStatusChanged(Order order, OrderStatus oldStatus) {
        if (order.getStatus() == null) return;

        boolean allowed =
                order.getStatus() == OrderStatus.APPROVED ||
                        order.getStatus() == OrderStatus.CANCELED ||
                        order.getStatus() == OrderStatus.COMPLETED;

        if (!allowed) return;

        String to = order.getUser().getEmail();

        String subject;
        String template;

        if (order.getStatus() == OrderStatus.APPROVED) {
            subject = "✅ Order təsdiqləndi — #" + order.getUserOrderNo();
            template = "mail/order-approved";
        } else if (order.getStatus() == OrderStatus.CANCELED) {
            subject = "⚠️ Order ləğv olundu — #" + order.getUserOrderNo();
            template = "mail/order-canceled";
        } else {
            subject = "🎉 Order tamamlandı — #" + order.getUserOrderNo();
            template = "mail/order-completed";
        }

        String orderUrl = baseUrl + "/order/" + order.getId();

        Context ctx = new Context();
        ctx.setVariable("order", order);
        ctx.setVariable("oldStatus", oldStatus != null ? oldStatus.name() : "-");
        ctx.setVariable("orderUrl", orderUrl);
        ctx.setVariable("pickupDate", order.getPickupDate() != null ? order.getPickupDate().format(DATE_FMT) : "-");
        ctx.setVariable("dropoffDate", order.getDropoffDate() != null ? order.getDropoffDate().format(DATE_FMT) : "-");

        sendHtml(to, subject, template, ctx);
    }

    // ---------------- ADMIN NOTIFY NEW PAID ORDER ----------------
    @Override
    public void notifyAdminNewPaidOrder(Payment payment) {
        Order order = payment.getOrder();
        String subject = "🟢 Yeni PAID order — #" + order.getUserOrderNo();

        String dashboardUrl = baseUrl + "/dashboard/orders/" + order.getId();

        Context ctx = new Context();
        ctx.setVariable("payment", payment);
        ctx.setVariable("order", order);
        ctx.setVariable("dashboardUrl", dashboardUrl);

        sendHtml(adminEmail, subject, "mail/admin-new-paid-order", ctx);
    }

    // ---------------- helper (kritik) ----------------
    private void sendHtml(String to, String subject, String templateName, Context ctx) {
        try {
            String html = templateEngine.process(templateName, ctx);

            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mime,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(to);
            helper.setFrom(fromEmail, fromName);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(mime);

            log.info("✅ HTML mail SENT. to={}, subject='{}', template={}", to, subject, templateName);
        } catch (Exception e) {
            // BURASI ÖNEMLİ: artık neden gitmediğini göreceksin
            log.error("❌ HTML mail FAILED. to={}, subject='{}', template={}", to, subject, templateName, e);
        }
    }
}