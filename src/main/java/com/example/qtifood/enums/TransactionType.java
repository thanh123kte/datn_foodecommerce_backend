package com.example.qtifood.enums;

public enum TransactionType {
    DEPOSIT,        // Nạp tiền
    WITHDRAW,       // Rút tiền
    EARN,           // Thu nhập (từ hệ thống tự động)
    MANUAL_INCOME,  // Thu nhập thủ công (COD, tiền mặt)
    REFUND,         // Hoàn tiền
    PAYMENT         // Thanh toán
}