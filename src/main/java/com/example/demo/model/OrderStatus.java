package com.example.demo.model;

public enum OrderStatus {
    PENDING,            // order yaradılıb, payment gözlənir
    PAID,               // payment success
    PAYMENT_FAILED,     // payment fail
    APPROVED,           // admin təsdiqi (sonra)
    CANCELED,
    COMPLETED
}
