package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Deliveries.CreateDeliveryDto;
import com.example.qtifood.dtos.Deliveries.UpdateDeliveryDto;
import com.example.qtifood.dtos.Deliveries.DeliveryResponseDto;
import com.example.qtifood.dtos.Deliveries.DriverIncomeStatsDto;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
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
    public List<DeliveryResponseDto> getDeliveriesByDriver(String driverId) {
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
    public List<DeliveryResponseDto> getDeliveriesByDriverAndStatus(String driverId, DeliveryStatus status) {
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
    
    @Override
    @Transactional(readOnly = true)
    public DriverIncomeStatsDto getDriverIncomeStats(String driverId, String period) {
        // Kiểm tra driver tồn tại
        driverRepository.findById(driverId)
            .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        LocalDateTime startDate;
        final LocalDateTime endDate = LocalDateTime.now();
        
        // Xác định khoảng thời gian dựa trên period
        switch (period.toLowerCase()) {
            case "daily":
                startDate = LocalDate.now().atStartOfDay();
                // endDate = LocalDate.now().atTime(LocalTime.MAX); // endDate đã được thiết lập ở trên
                break;
            case "weekly":
                // Tuần bắt đầu từ thứ 2
                LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                startDate = startOfWeek.atStartOfDay();
                break;
            case "monthly":
                startDate = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                break;
            default:
                throw new IllegalArgumentException("Invalid period. Use 'daily', 'weekly', or 'monthly'");
        }
        
        // Lấy danh sách deliveries đã hoàn thành trong khoảng thời gian
        List<Delivery> deliveries = deliveryRepository.findByDriverIdAndStatus(driverId, DeliveryStatus.COMPLETED)
            .stream()
            .filter(d -> d.getCompletedAt() != null 
                && !d.getCompletedAt().isBefore(startDate) 
                && !d.getCompletedAt().isAfter(endDate))
            .collect(Collectors.toList());
        
        // Tính toán thống kê
        int totalDeliveries = deliveries.size();
        BigDecimal totalIncome = deliveries.stream()
            .map(d -> d.getDriverIncome() != null ? d.getDriverIncome() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalShippingFee = deliveries.stream()
            .map(d -> d.getShippingFee() != null ? d.getShippingFee() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDistance = deliveries.stream()
            .map(d -> d.getDistanceKm() != null ? d.getDistanceKm() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageIncomePerDelivery = totalDeliveries > 0 
            ? totalIncome.divide(BigDecimal.valueOf(totalDeliveries), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        BigDecimal averageDistancePerDelivery = totalDeliveries > 0
            ? totalDistance.divide(BigDecimal.valueOf(totalDeliveries), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        return DriverIncomeStatsDto.builder()
            .period(period)
            .startDate(startDate)
            .endDate(endDate)
            .totalDeliveries(totalDeliveries)
            .totalIncome(totalIncome)
            .totalShippingFee(totalShippingFee)
            .totalDistance(totalDistance)
            .averageIncomePerDelivery(averageIncomePerDelivery)
            .averageDistancePerDelivery(averageDistancePerDelivery)
            .build();
    }
}