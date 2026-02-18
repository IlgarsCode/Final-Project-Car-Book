package com.example.demo.services;

import com.example.demo.dto.payment.PaymentConfirmDto;
import com.example.demo.model.Payment;

public interface PaymentService {
    Payment startPayment(String userEmail, Long orderId);
    Payment confirmAndProcess(String userEmail, Long paymentId, PaymentConfirmDto dto);
    Payment getPaymentForUser(String userEmail, Long paymentId);
    Payment pollAndMaybeFinalize(String userEmail, Long paymentId);
}
