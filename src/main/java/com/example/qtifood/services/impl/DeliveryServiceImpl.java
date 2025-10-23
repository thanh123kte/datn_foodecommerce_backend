package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Deliveries.CreateDeliveryDto;
import com.example.qtifood.dtos.Deliveries.UpdateDeliveryDto;
import com.example.qtifood.dtos.Deliveries.DeliveryResponseDto;
import com.example.qtifood.entities.Delivery;
import com.example.qtifood.entities.Order;
import com.example.qtifood.entities.Driver;
import com.example.qtifood.enums.DeliveryStatus;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.mappers.DeliveryMapper;
import com.example.qtifood.repositories.DeliveryRepository;
import com.example.qtifood.repositories.OrderRepository;
import com.example.qtifood.repositories.DriverRepository;
import com.example.qtifood.services.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    @Transactional
    public DeliveryResponseDto createDelivery(CreateDeliveryDto dto) {
        Delivery delivery = deliveryMapper.toEntity(dto);
        
        // Set order
        Order order = orderRepository.findById(dto.getOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        delivery.setOrder(order);
        
        // Set driver
        Driver driver = driverRepository.findById(dto.getDriverId())
            .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        delivery.setDriver(driver);
        
        // Set default status
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        
        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional
    public DeliveryResponseDto updateDelivery(Long id, UpdateDeliveryDto dto) {
        Delivery delivery = deliveryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));
        
        // Update driver if provided
        if (dto.getDriverId() != null) {
            Driver driver = driverRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
            delivery.setDriver(driver);
        }
        
        // Use mapper to update other fields
        deliveryMapper.updateDeliveryFromDto(dto, delivery);
        
        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional
    public void deleteDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));
        deliveryRepository.delete(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponseDto getDeliveryById(Long id) {
        return deliveryMapper.toDto(deliveryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Delivery not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDto> getAllDeliveries() {
        return deliveryRepository.findAll().stream()
            .map(deliveryMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDto> getDeliveriesByDriver(Long driverId) {
        return deliveryRepository.findByDriverId(driverId).stream()
            .map(deliveryMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponseDto getDeliveryByOrder(Long orderId) {
        return deliveryMapper.toDto(deliveryRepository.findByOrderId(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for this order")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDto> getDeliveriesByStatus(DeliveryStatus status) {
        return deliveryRepository.findByStatus(status).stream()
            .map(deliveryMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponseDto> getDeliveriesByDriverAndStatus(Long driverId, DeliveryStatus status) {
        return deliveryRepository.findByDriverIdAndStatus(driverId, status).stream()
            .map(deliveryMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeliveryResponseDto updateDeliveryStatus(Long id, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));
        
        delivery.setStatus(status);
        
        // Set timestamps based on status
        LocalDateTime now = LocalDateTime.now();
        if (status == DeliveryStatus.PICKED_UP && delivery.getStartedAt() == null) {
            delivery.setStartedAt(now);
        } else if (status == DeliveryStatus.COMPLETED && delivery.getCompletedAt() == null) {
            delivery.setCompletedAt(now);
        }
        
        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }
}