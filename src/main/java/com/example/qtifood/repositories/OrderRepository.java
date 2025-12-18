package com.example.qtifood.repositories;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.qtifood.entities.Order;
import com.example.qtifood.enums.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(String customerId);
    List<Order> findByStoreId(Long storeId);
    List<Order> findByDriverId(String driverId);
    List<Order> findByOrderStatus(OrderStatus status);
    List<Order> findByStoreIdAndOrderStatusAndUpdatedAtBetween(Long storeId, OrderStatus status, LocalDateTime start, LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.store.id = :storeId AND o.orderStatus IN :statuses AND o.createdAt BETWEEN :start AND :end")
    List<Order> findByStoreIdAndOrderStatusInAndCreatedAtBetween(
            @Param("storeId") Long storeId,
            @Param("statuses") List<OrderStatus> statuses,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
