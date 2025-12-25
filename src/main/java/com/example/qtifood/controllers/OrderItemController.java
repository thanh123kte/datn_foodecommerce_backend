package com.example.qtifood.controllers;

import com.example.qtifood.dtos.OrderItems.CreateOrderItemDto;
import com.example.qtifood.dtos.OrderItems.UpdateOrderItemDto;
import com.example.qtifood.dtos.OrderItems.OrderItemResponseDto;
import com.example.qtifood.services.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;

    @PostMapping
    public ResponseEntity<OrderItemResponseDto> createOrderItem(@RequestBody CreateOrderItemDto dto) {
        return ResponseEntity.ok(orderItemService.createOrderItem(dto));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<OrderItemResponseDto>> createOrderItems(@RequestBody List<CreateOrderItemDto> dtos) {
        return ResponseEntity.ok(orderItemService.createOrderItems(dtos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemResponseDto> updateOrderItem(@PathVariable Long id, @RequestBody UpdateOrderItemDto dto) {
        return ResponseEntity.ok(orderItemService.updateOrderItem(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.ok("Order item deleted successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponseDto> getOrderItemById(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderItemResponseDto>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemResponseDto>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByOrderId(orderId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<OrderItemResponseDto>> getOrderItemsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByProductId(productId));
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<String> deleteOrderItemsByOrderId(@PathVariable Long orderId) {
        orderItemService.deleteOrderItemsByOrderId(orderId);
        return ResponseEntity.ok("All order items for the order deleted successfully");
    }
}