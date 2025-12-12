package com.example.qtifood.services;

import com.example.qtifood.dtos.Orders.OrderResponseDto;

public interface DriverAssignmentService {
    
    /**
     * Tự động tìm và gán tài xế online cho đơn hàng
     * @param orderId ID đơn hàng
     * @return OrderResponseDto với thông tin tài xế đã gán
     */
    OrderResponseDto assignDriverToOrder(Long orderId);
    
    /**
     * Xử lý thanh toán khi tài xế giao hàng thành công
     * - Cộng tiền cho shop (trừ phí sàn 12%)
     * - Cộng tiền cho admin (phí sàn từ shop + driver)
     * - Cộng tiền giao hàng cho driver (trừ phí sàn)
     * 
     * @param orderId ID đơn hàng đã giao thành công
     */
    void processDeliveryPayment(Long orderId);
}
