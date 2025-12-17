package com.example.qtifood.enums;

public enum OrderStatus {
    PENDING,    // Chờ xác nhận
    CONFIRMED,  // Đã xác nhận
    PREPARING,  // Đang chuẩn bị
    PREPARED,   // Đã chuẩn bị xong
    SHIPPING,   // Đang giao hàng
    DELIVERED,  // Đã giao
    REVIEWED,   // Đã đánh giá
    CANCELLED   // Đã hủy
}