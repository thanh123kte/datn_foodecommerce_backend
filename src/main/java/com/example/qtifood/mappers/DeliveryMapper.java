package com.example.qtifood.mappers;

import com.example.qtifood.dtos.Deliveries.CreateDeliveryDto;
import com.example.qtifood.dtos.Deliveries.UpdateDeliveryDto;
import com.example.qtifood.dtos.Deliveries.DeliveryResponseDto;
import com.example.qtifood.entities.Delivery;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {
    
    public Delivery toEntity(CreateDeliveryDto dto) {
        Delivery delivery = new Delivery();
        delivery.setDistanceKm(dto.getDistanceKm());
        delivery.setGoodsAmount(dto.getGoodsAmount());
        delivery.setShippingFee(dto.getShippingFee());
        delivery.setDriverIncome(dto.getDriverIncome());
        delivery.setPaymentMethod(dto.getPaymentMethod());
        delivery.setStoreName(dto.getStoreName());
        delivery.setShippingAddress(dto.getShippingAddress());
        delivery.setCustomerName(dto.getCustomerName());
        return delivery;
    }
    
    public DeliveryResponseDto toDto(Delivery delivery) {
        DeliveryResponseDto dto = new DeliveryResponseDto();
        dto.setId(delivery.getId());
        dto.setOrderId(delivery.getOrder() != null ? delivery.getOrder().getId() : null);
        dto.setDriverId(delivery.getDriver() != null ? delivery.getDriver().getId() : null);
        dto.setDriverName(delivery.getDriver() != null ? delivery.getDriver().getFullName() : null);
        dto.setDistanceKm(delivery.getDistanceKm());
        dto.setGoodsAmount(delivery.getGoodsAmount());
        dto.setShippingFee(delivery.getShippingFee());
        dto.setDriverIncome(delivery.getDriverIncome());
        dto.setPaymentMethod(delivery.getPaymentMethod());
        dto.setStoreName(delivery.getStoreName());
        dto.setShippingAddress(delivery.getShippingAddress());
        dto.setCustomerName(delivery.getCustomerName());
        dto.setStatus(delivery.getStatus());
        dto.setStartedAt(delivery.getStartedAt());
        dto.setCompletedAt(delivery.getCompletedAt());
        return dto;
    }
    
    public void updateDeliveryFromDto(UpdateDeliveryDto dto, Delivery delivery) {
        if (dto.getDistanceKm() != null) delivery.setDistanceKm(dto.getDistanceKm());
        if (dto.getGoodsAmount() != null) delivery.setGoodsAmount(dto.getGoodsAmount());
        if (dto.getShippingFee() != null) delivery.setShippingFee(dto.getShippingFee());
        if (dto.getDriverIncome() != null) delivery.setDriverIncome(dto.getDriverIncome());
        if (dto.getPaymentMethod() != null) delivery.setPaymentMethod(dto.getPaymentMethod());
        if (dto.getStoreName() != null) delivery.setStoreName(dto.getStoreName());
        if (dto.getShippingAddress() != null) delivery.setShippingAddress(dto.getShippingAddress());
        if (dto.getCustomerName() != null) delivery.setCustomerName(dto.getCustomerName());
        if (dto.getStatus() != null) delivery.setStatus(dto.getStatus());
        if (dto.getStartedAt() != null) delivery.setStartedAt(dto.getStartedAt());
        if (dto.getCompletedAt() != null) delivery.setCompletedAt(dto.getCompletedAt());
    }
}