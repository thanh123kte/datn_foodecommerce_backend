package com.example.qtifood.mappers;

import com.example.qtifood.dtos.Orders.CreateOrderDto;
import com.example.qtifood.dtos.Orders.UpdateOrderDto;
import com.example.qtifood.dtos.Orders.OrderResponseDto;
import com.example.qtifood.entities.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final OrderItemMapper orderItemMapper;
    
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
        
        // Customer info
        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getId());
            dto.setCustomerName(order.getCustomer().getFullName());
            dto.setCustomerPhone(order.getCustomer().getPhone());
            dto.setCustomerAvatar(buildFileUrl(order.getCustomer().getAvatarUrl()));
        }
        
        // Store info
        if (order.getStore() != null) {
            dto.setStoreId(order.getStore().getId());
            dto.setStoreName(order.getStore().getName());
        }
        
        // Driver info
        if (order.getDriver() != null) {
            dto.setDriverId(order.getDriver().getId());
            dto.setDriverName(order.getDriver().getFullName());
            dto.setDriverPhone(order.getDriver().getPhone());
        }
        
        // Shipping address info
        if (order.getShippingAddress() != null) {
            dto.setShippingAddressId(order.getShippingAddress().getId());
            dto.setShippingAddress(order.getShippingAddress().getAddress());
            dto.setShippingReceiver(order.getShippingAddress().getReceiver());
            dto.setShippingPhone(order.getShippingAddress().getPhone());
        }

        // Items
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            List<com.example.qtifood.dtos.OrderItems.OrderItemResponseDto> items = order.getOrderItems()
                .stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
            dto.setItems(items);

            BigDecimal itemsTotal = items.stream()
                .map(i -> i.getTotalPrice() != null ? i.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setItemsTotal(itemsTotal);
        } else {
            dto.setItemsTotal(BigDecimal.ZERO);
        }

        // Discount (placeholder, vouchers not yet applied)
        dto.setDiscountAmount(BigDecimal.ZERO);
        
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
    
    /**
     * Normalize uploaded file paths so the frontend can load them directly.
     * Accepts already absolute URLs, or relative paths stored in DB (e.g. users/xxx.png).
     */
    private String buildFileUrl(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        String trimmed = path.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        if (trimmed.startsWith("/uploads/")) {
            return trimmed;
        }
        if (trimmed.startsWith("/")) {
            return "/uploads" + trimmed;
        }
        return "/uploads/" + trimmed;
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
