package com.example.demo.services;

import com.example.demo.dto.contact.ContactDto;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.Payment;

public interface EmailService {
    void sendContactMail(String name, String email, String subject, String message);
    void sendContactMail(ContactDto dto);

    void sendOtpMail(String toEmail, String subject, String text);

    void sendOrderCreatedPending(Order order);
    void sendPaymentSucceeded(Payment payment);
    void sendPaymentFailed(Payment payment);
    void sendOrderStatusChanged(Order order, OrderStatus oldSt);
    void notifyAdminNewPaidOrder(Payment payment);
}