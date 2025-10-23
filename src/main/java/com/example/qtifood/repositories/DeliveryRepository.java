package com.example.qtifood.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.qtifood.entities.Delivery;
import com.example.qtifood.enums.DeliveryStatus;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByDriverId(Long driverId);
    Optional<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByStatus(DeliveryStatus status);
    List<Delivery> findByDriverIdAndStatus(Long driverId, DeliveryStatus status);
}