package com.example.qtifood.services;

import com.example.qtifood.dtos.Orders.CreateOrderDto;
import com.example.qtifood.dtos.Orders.UpdateOrderDto;
import com.example.qtifood.dtos.Orders.OrderResponseDto;
import com.example.qtifood.dtos.Orders.SalesStatsDto;
import com.example.qtifood.dtos.Orders.TopProductDto;
import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(CreateOrderDto dto);
    OrderResponseDto updateOrder(Long id, UpdateOrderDto dto);
    void deleteOrder(Long id);
    OrderResponseDto getOrderById(Long id);
    List<OrderResponseDto> getAllOrders();
    List<OrderResponseDto> getOrdersByCustomer(String customerId);
    List<OrderResponseDto> getOrdersByStore(Long storeId);
    List<OrderResponseDto> getOrdersByDriver(String driverId);
    OrderResponseDto updateOrderStatus(Long id, String status);
    List<TopProductDto> getTopSellingProducts(Integer limit);
    List<TopProductDto> getTopSellingProductsByStore(Long storeId, Integer limit);
    
    /**
     * Update only payment status of an order (used by payment gateway callbacks)
     */
    void updatePaymentStatus(Long orderId, com.example.qtifood.enums.PaymentStatus status);

    // Seller stats
    SalesStatsDto getStoreSalesStats(Long storeId, String period);
}
