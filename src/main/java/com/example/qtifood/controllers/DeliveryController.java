package com.example.qtifood.controllers;

import com.example.qtifood.dtos.Deliveries.CreateDeliveryDto;
import com.example.qtifood.dtos.Deliveries.UpdateDeliveryDto;
import com.example.qtifood.dtos.Deliveries.DeliveryResponseDto;
import com.example.qtifood.enums.DeliveryStatus;
import com.example.qtifood.services.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryResponseDto> createDelivery(@RequestBody CreateDeliveryDto dto) {
        return ResponseEntity.ok(deliveryService.createDelivery(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryResponseDto> updateDelivery(@PathVariable Long id, @RequestBody UpdateDeliveryDto dto) {
        return ResponseEntity.ok(deliveryService.updateDelivery(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.ok("Delivery deleted successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDto> getDeliveryById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));
    }

    @GetMapping
    public ResponseEntity<List<DeliveryResponseDto>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<DeliveryResponseDto>> getDeliveriesByDriver(@PathVariable String driverId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByDriver(driverId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponseDto> getDeliveryByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getDeliveryByOrder(orderId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DeliveryResponseDto>> getDeliveriesByStatus(@PathVariable DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(status));
    }

    @GetMapping("/driver/{driverId}/status/{status}")
    public ResponseEntity<List<DeliveryResponseDto>> getDeliveriesByDriverAndStatus(@PathVariable String driverId, @PathVariable DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByDriverAndStatus(driverId, status));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DeliveryResponseDto> updateDeliveryStatus(@PathVariable Long id, @RequestParam DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(id, status));
    }
}