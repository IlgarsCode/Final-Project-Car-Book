package com.example.demo.services;

import com.example.demo.dto.contact.ContactDto;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.Payment;

public interface EmailService {
    void sendContactMail(String name, String email, String subject, String message);
    void sendContactMail(ContactDto dto);

    void sendOtpMail(String toEmail, String subject, String text);

    // ✅ Order & Payment mail-ləri
    void sendOrderCreatedPending(Order order);                    // (1)
    void sendPaymentSucceeded(Payment payment);                   // (2)
    void sendPaymentFailed(Payment payment);                      // (3)
    void sendOrderStatusChanged(Order order, OrderStatus oldSt);  // (4)
    void notifyAdminNewPaidOrder(Payment payment);                // (7)
}
