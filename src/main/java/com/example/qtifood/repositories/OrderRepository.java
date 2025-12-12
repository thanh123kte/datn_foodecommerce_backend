package com.example.qtifood.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.qtifood.entities.Order;
import com.example.qtifood.enums.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(String customerId);
    List<Order> findByStoreId(Long storeId);
    List<Order> findByDriverId(String driverId);
    List<Order> findByOrderStatus(OrderStatus status);
}
