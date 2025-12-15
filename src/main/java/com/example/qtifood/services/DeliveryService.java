package com.example.qtifood.services;

import com.example.qtifood.dtos.Deliveries.CreateDeliveryDto;
import com.example.qtifood.dtos.Deliveries.UpdateDeliveryDto;
import com.example.qtifood.dtos.Deliveries.DeliveryResponseDto;
import com.example.qtifood.dtos.Deliveries.DriverIncomeStatsDto;
import com.example.qtifood.enums.DeliveryStatus;
import java.util.List;

public interface DeliveryService {
    DeliveryResponseDto createDelivery(CreateDeliveryDto dto);
    DeliveryResponseDto updateDelivery(Long id, UpdateDeliveryDto dto);
    void deleteDelivery(Long id);
    DeliveryResponseDto getDeliveryById(Long id);
    List<DeliveryResponseDto> getAllDeliveries();
    List<DeliveryResponseDto> getDeliveriesByDriver(String driverId);
    DeliveryResponseDto getDeliveryByOrder(Long orderId);
    List<DeliveryResponseDto> getDeliveriesByStatus(DeliveryStatus status);
    List<DeliveryResponseDto> getDeliveriesByDriverAndStatus(String driverId, DeliveryStatus status);
    DeliveryResponseDto updateDeliveryStatus(Long id, DeliveryStatus status);
    
    // Thống kê thu nhập tài xế
    DriverIncomeStatsDto getDriverIncomeStats(String driverId, String period);
}