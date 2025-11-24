package com.example.qtifood.mappers;

import com.example.qtifood.dtos.Orders.CreateOrderDto;
import com.example.qtifood.dtos.Orders.UpdateOrderDto;
import com.example.qtifood.dtos.Orders.OrderResponseDto;
import com.example.qtifood.entities.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    
    public Order toEntity(CreateOrderDto dto) {
        Order order = new Order();
        // totalAmount, shippingFee, expectedDeliveryTime will be calculated by service
        order.setAdminVoucherId(dto.getAdminVoucherId());
        order.setSellerVoucherId(dto.getSellerVoucherId());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setNote(dto.getNote());
        return order;
    }
    
    public OrderResponseDto toDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomer() != null ? order.getCustomer().getId() : null);
        dto.setStoreId(order.getStore() != null ? order.getStore().getId() : null);
        dto.setDriverId(order.getDriver() != null ? order.getDriver().getId() : null);
        dto.setShippingAddressId(order.getShippingAddress() != null ? order.getShippingAddress().getId() : null);
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingFee(order.getShippingFee());
        dto.setAdminVoucherId(order.getAdminVoucherId());
        dto.setSellerVoucherId(order.getSellerVoucherId());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPaidAt(order.getPaidAt());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setNote(order.getNote());
        dto.setCancelReason(order.getCancelReason());
        dto.setExpectedDeliveryTime(order.getExpectedDeliveryTime());
        dto.setRatingStatus(order.getRatingStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }
    
    public void updateOrderFromDto(UpdateOrderDto dto, Order order) {
        if (dto.getTotalAmount() != null) order.setTotalAmount(dto.getTotalAmount());
        if (dto.getShippingFee() != null) order.setShippingFee(dto.getShippingFee());
        if (dto.getAdminVoucherId() != null) order.setAdminVoucherId(dto.getAdminVoucherId());
        if (dto.getSellerVoucherId() != null) order.setSellerVoucherId(dto.getSellerVoucherId());
        if (dto.getPaymentMethod() != null) order.setPaymentMethod(dto.getPaymentMethod());
        if (dto.getNote() != null) order.setNote(dto.getNote());
        if (dto.getExpectedDeliveryTime() != null) order.setExpectedDeliveryTime(dto.getExpectedDeliveryTime());
        if (dto.getOrderStatus() != null) order.setOrderStatus(dto.getOrderStatus());
        if (dto.getCancelReason() != null) order.setCancelReason(dto.getCancelReason());
        if (dto.getRatingStatus() != null) order.setRatingStatus(dto.getRatingStatus());
        if (dto.getPaidAt() != null) order.setPaidAt(dto.getPaidAt());
    }
}
