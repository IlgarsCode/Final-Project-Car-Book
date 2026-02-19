package com.example.demo.services.impl;

import com.example.demo.dto.payment.PaymentConfirmDto;
import com.example.demo.model.*;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.CartService;
import com.example.demo.services.EmailService;
import com.example.demo.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    // ✅ NEW
    private final EmailService emailService;

    @Override
    public Payment startPayment(String userEmail, Long orderId) {

        var user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));

        var order = orderRepository.findByIdAndUser_Id(orderId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order tapılmadı"));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu order artıq ödənilib");
        }
        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu order üçün payment mümkün deyil");
        }

        var last = paymentRepository.findTopByOrder_IdOrderByIdDesc(order.getId()).orElse(null);
        if (last != null && last.getStatus() == PaymentStatus.PROCESSING) {
            return last;
        }

        Payment p = new Payment();
        p.setOrder(order);
        p.setAmount(order.getTotalAmount());
        p.setCurrency("USD");
        p.setStatus(PaymentStatus.PROCESSING);
        p.setIntentId("pi_" + UUID.randomUUID().toString().replace("-", ""));
        p.setFailureReason(null);
        p.setCompletedAt(null);

        return paymentRepository.save(p);
    }

    @Override
    public Payment confirmAndProcess(String userEmail, Long paymentId, PaymentConfirmDto dto) {

        Payment p = getPaymentForUser(userEmail, paymentId);

        if (p.getStatus() != PaymentStatus.PROCESSING) return p;

        String clean = dto.getCardNumber() == null ? "" : dto.getCardNumber().replace(" ", "").trim();

        if ("4000000000000002".equals(clean)) {
            p.setFailureReason("WILL_FAIL");
        } else {
            p.setFailureReason(null);
        }

        return paymentRepository.save(p);
    }

    @Override
    public Payment getPaymentForUser(String userEmail, Long paymentId) {
        var user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tapılmadı"));

        Payment p = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment tapılmadı"));

        if (p.getOrder() == null || p.getOrder().getUser() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Payment order əlaqəsi pozulub");
        }

        if (!p.getOrder().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return p;
    }

    @Override
    public Payment pollAndMaybeFinalize(String userEmail, Long paymentId) {
        Payment p = getPaymentForUser(userEmail, paymentId);

        if (p.getStatus() != PaymentStatus.PROCESSING) return p;

        long seconds = Duration.between(p.getCreatedAt(), LocalDateTime.now()).getSeconds();
        if (seconds < 2) return p;

        boolean success = !"WILL_FAIL".equalsIgnoreCase(p.getFailureReason());

        if (success) {
            p.setStatus(PaymentStatus.SUCCEEDED);
            p.setCompletedAt(LocalDateTime.now());
            paymentRepository.save(p);

            Order o = p.getOrder();
            o.setStatus(OrderStatus.PAID);
            orderRepository.save(o);

            cartService.clearCart(userEmail);

            // ✅ (2) user mail + (7) admin notify
            emailService.sendPaymentSucceeded(p);
            emailService.notifyAdminNewPaidOrder(p);

            return p;
        } else {
            p.setStatus(PaymentStatus.FAILED);
            p.setFailureReason("Card declined");
            p.setCompletedAt(LocalDateTime.now());
            paymentRepository.save(p);

            Order o = p.getOrder();
            o.setStatus(OrderStatus.PAYMENT_FAILED);
            orderRepository.save(o);

            // ✅ (3) user mail
            emailService.sendPaymentFailed(p);

            return p;
        }
    }
}
