package com.example.qtifood.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.qtifood.dtos.Orders.TopProductDto;
import com.example.qtifood.entities.OrderItem;
import com.example.qtifood.enums.OrderStatus;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByProductId(Long productId);
    void deleteByOrderId(Long orderId);

        @Query("SELECT new com.example.qtifood.dtos.Orders.TopProductDto("
            + "oi.product.id, oi.product.name, SUM(oi.quantity), SUM(oi.price * oi.quantity)) "
            + "FROM OrderItem oi "
            + "WHERE oi.order.orderStatus IN :statuses "
            + "GROUP BY oi.product.id, oi.product.name "
            + "ORDER BY SUM(oi.quantity) DESC")
        List<TopProductDto> findTopProductsByOrderStatusIn(@Param("statuses") List<OrderStatus> statuses, Pageable pageable);

        @Query("SELECT new com.example.qtifood.dtos.Orders.TopProductDto("
            + "oi.product.id, oi.product.name, SUM(oi.quantity), SUM(oi.price * oi.quantity)) "
            + "FROM OrderItem oi "
            + "WHERE oi.order.store.id = :storeId AND oi.order.orderStatus IN :statuses "
            + "GROUP BY oi.product.id, oi.product.name "
            + "ORDER BY SUM(oi.quantity) DESC")
        List<TopProductDto> findTopProductsByStoreAndOrderStatusIn(
            @Param("storeId") Long storeId,
            @Param("statuses") List<OrderStatus> statuses,
            Pageable pageable);
}