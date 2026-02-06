package com.example.qtifood.services;

import com.example.qtifood.dtos.OrderItems.CreateOrderItemDto;
import com.example.qtifood.dtos.OrderItems.UpdateOrderItemDto;
import com.example.qtifood.dtos.OrderItems.OrderItemResponseDto;
import java.util.List;

public interface OrderItemService {
    OrderItemResponseDto createOrderItem(CreateOrderItemDto dto);
    List<OrderItemResponseDto> createOrderItems(List<CreateOrderItemDto> dtos);
    OrderItemResponseDto updateOrderItem(Long id, UpdateOrderItemDto dto);
    void deleteOrderItem(Long id);
    OrderItemResponseDto getOrderItemById(Long id);
    List<OrderItemResponseDto> getAllOrderItems();
    List<OrderItemResponseDto> getOrderItemsByOrderId(Long orderId);
    List<OrderItemResponseDto> getOrderItemsByProductId(Long productId);
    void deleteOrderItemsByOrderId(Long orderId);
    
    /**
     * Thêm nhiều items vào order một lúc
     * @param orderId ID của order
     * @param items Danh sách items cần thêm
     * @return Danh sách OrderItemResponseDto đã được tạo
     */
    List<OrderItemResponseDto> addItemsToOrder(Long orderId, List<CreateOrderItemDto> items);
}