package com.example.demo.model;

public enum OrderStatus {
    PENDING,     // checkout edilib, hələ təsdiq yox
    APPROVED,    // admin təsdiq edər (sonra)
    CANCELED,
    COMPLETED
}
