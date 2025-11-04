package com.example.qtifood.services;

import com.example.qtifood.dtos.Orders.CreateOrderDto;
import com.example.qtifood.dtos.Orders.UpdateOrderDto;
import com.example.qtifood.dtos.Orders.OrderResponseDto;
import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(CreateOrderDto dto);
    OrderResponseDto updateOrder(Long id, UpdateOrderDto dto);
    void deleteOrder(Long id);
    OrderResponseDto getOrderById(Long id);
    List<OrderResponseDto> getAllOrders();
    List<OrderResponseDto> getOrdersByCustomer(String customerId);
    List<OrderResponseDto> getOrdersByStore(Long storeId);
    List<OrderResponseDto> getOrdersByDriver(Long driverId);
    OrderResponseDto updateOrderStatus(Long id, String status);
}
