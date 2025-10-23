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
        delivery.setPickupLat(dto.getPickupLat());
        delivery.setPickupLng(dto.getPickupLng());
        delivery.setDropoffLat(dto.getDropoffLat());
        delivery.setDropoffLng(dto.getDropoffLng());
        delivery.setDistanceKm(dto.getDistanceKm());
        return delivery;
    }
    
    public DeliveryResponseDto toDto(Delivery delivery) {
        DeliveryResponseDto dto = new DeliveryResponseDto();
        dto.setId(delivery.getId());
        dto.setOrderId(delivery.getOrder() != null ? delivery.getOrder().getId() : null);
        dto.setDriverId(delivery.getDriver() != null ? delivery.getDriver().getId() : null);
        dto.setDriverName(delivery.getDriver() != null ? delivery.getDriver().getFullName() : null);
        dto.setPickupLat(delivery.getPickupLat());
        dto.setPickupLng(delivery.getPickupLng());
        dto.setDropoffLat(delivery.getDropoffLat());
        dto.setDropoffLng(delivery.getDropoffLng());
        dto.setDistanceKm(delivery.getDistanceKm());
        dto.setStatus(delivery.getStatus());
        dto.setStartedAt(delivery.getStartedAt());
        dto.setCompletedAt(delivery.getCompletedAt());
        return dto;
    }
    
    public void updateDeliveryFromDto(UpdateDeliveryDto dto, Delivery delivery) {
        if (dto.getPickupLat() != null) delivery.setPickupLat(dto.getPickupLat());
        if (dto.getPickupLng() != null) delivery.setPickupLng(dto.getPickupLng());
        if (dto.getDropoffLat() != null) delivery.setDropoffLat(dto.getDropoffLat());
        if (dto.getDropoffLng() != null) delivery.setDropoffLng(dto.getDropoffLng());
        if (dto.getDistanceKm() != null) delivery.setDistanceKm(dto.getDistanceKm());
        if (dto.getStatus() != null) delivery.setStatus(dto.getStatus());
        if (dto.getStartedAt() != null) delivery.setStartedAt(dto.getStartedAt());
        if (dto.getCompletedAt() != null) delivery.setCompletedAt(dto.getCompletedAt());
    }
}