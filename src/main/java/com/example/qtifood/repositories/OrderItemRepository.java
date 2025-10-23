package com.example.qtifood.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.qtifood.entities.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByProductId(Long productId);
    void deleteByOrderId(Long orderId);
}