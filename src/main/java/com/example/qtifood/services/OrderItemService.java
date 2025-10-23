package com.example.qtifood.services;

import com.example.qtifood.dtos.OrderItems.CreateOrderItemDto;
import com.example.qtifood.dtos.OrderItems.UpdateOrderItemDto;
import com.example.qtifood.dtos.OrderItems.OrderItemResponseDto;
import java.util.List;

public interface OrderItemService {
    OrderItemResponseDto createOrderItem(CreateOrderItemDto dto);
    OrderItemResponseDto updateOrderItem(Long id, UpdateOrderItemDto dto);
    void deleteOrderItem(Long id);
    OrderItemResponseDto getOrderItemById(Long id);
    List<OrderItemResponseDto> getAllOrderItems();
    List<OrderItemResponseDto> getOrderItemsByOrderId(Long orderId);
    List<OrderItemResponseDto> getOrderItemsByProductId(Long productId);
    void deleteOrderItemsByOrderId(Long orderId);
}