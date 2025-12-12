package com.example.qtifood.controllers;

import com.example.qtifood.dtos.Orders.CreateOrderDto;
import com.example.qtifood.dtos.Orders.UpdateOrderDto;
import com.example.qtifood.dtos.Orders.OrderResponseDto;
import com.example.qtifood.services.DriverAssignmentService;
import com.example.qtifood.services.OrderService;
import com.example.qtifood.services.FcmService;
import com.example.qtifood.enums.OrderStatus;
import com.example.qtifood.services.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final FcmService fcmService;
    private final StoreService storeService;
    private final DriverAssignmentService driverAssignmentService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody CreateOrderDto dto) {
        log.info("[OrderController] Nhận yêu cầu tạo đơn hàng: {}", dto);
        OrderResponseDto order = orderService.createOrder(dto);
        log.info("[OrderController] Đã tạo đơn hàng mới: id={}, customerId={}", order.getId(), order.getCustomerId());
        // Gửi FCM cho customer
        String title = "Đơn hàng mới";
        String body = "Bạn vừa tạo đơn hàng #" + order.getId();
        log.info("[OrderController] Bắn FCM cho customerId={}, title={}, body={}", order.getCustomerId(), title, body);
        fcmService.sendNotification(order.getCustomerId(), title, body, "ORDER", Map.of("orderId", String.valueOf(order.getId())));

        // Gửi FCM cho seller
        String titleSeller = "Bạn vừa nhận được đơn hàng mới";
        String bodySeller = "Bạn vừa nhận được đơn hàng mới #" + order.getId();
        String sellerId = null;
        try {
            sellerId = storeService.getStoreById(order.getStoreId()).getOwnerId();
        } catch (Exception e) {
            log.error("[OrderController] Không lấy được ownerId của storeId={}: {}", order.getStoreId(), e.getMessage());
        }
        if (sellerId != null) {
            log.info("[OrderController] Bắn FCM cho sellerId={}, title={}, body={}", sellerId, titleSeller, bodySeller);
            fcmService.sendNotification(sellerId, titleSeller, bodySeller, "ORDER", Map.of("orderId", String.valueOf(order.getId())));
        }
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id, @RequestBody UpdateOrderDto dto) {
        return ResponseEntity.ok(orderService.updateOrder(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(orderService.getOrdersByStore(storeId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByDriver(@PathVariable String driverId) {
        return ResponseEntity.ok(orderService.getOrdersByDriver(driverId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        log.info("[OrderController] Nhận yêu cầu cập nhật trạng thái đơn hàng: id={}, status={}", id, status);
        OrderResponseDto order = orderService.updateOrderStatus(id, status.name());
        log.info("[OrderController] Đã cập nhật trạng thái đơn hàng: id={}, status={}, customerId={}", order.getId(), status, order.getCustomerId());
        // Gửi FCM cho customer
        String title = "Cập nhật trạng thái đơn";
        String body = "Đơn hàng #" + order.getId() + " đã chuyển sang trạng thái " + status.name();
        log.info("[OrderController] Bắn FCM cho customerId={}, title={}, body={}", order.getCustomerId(), title, body);
        fcmService.sendNotification(order.getCustomerId(), title, body, "ORDER_STATUS", Map.of("orderId", String.valueOf(order.getId()), "status", status.name()));
        return ResponseEntity.ok(order);
    }
    
    /**
     * API tự động gán tài xế cho đơn hàng khi chuyển sang PREPARED
     * Tìm tài xế ONLINE, gán vào đơn, chuyển trạng thái sang SHIPPING
     * Gửi notification cho tài xế và lưu tracking vào Firebase Realtime DB
     * 
     * @param orderId ID đơn hàng (phải có status = PREPARED)
     * @return OrderResponseDto với thông tin tài xế đã gán
     */
    @PostMapping("/{orderId}/assign-driver")
    public ResponseEntity<OrderResponseDto> assignDriver(@PathVariable Long orderId) {
        log.info("[OrderController] Nhận yêu cầu gán tài xế cho đơn hàng: orderId={}", orderId);
        OrderResponseDto order = driverAssignmentService.assignDriverToOrder(orderId);
        log.info("[OrderController] Đã gán tài xế cho đơn hàng: orderId={}, driverId={}, status={}", 
                order.getId(), order.getDriverId(), order.getOrderStatus());
        return ResponseEntity.ok(order);
    }
    
    /**
     * API xử lý thanh toán khi tài xế giao hàng thành công
     * - Cộng tiền cho shop (trừ phí sàn 12%)
     * - Cộng tiền giao hàng cho driver (trừ phí sàn động)
     * - Ghi nhận phí sàn cho admin
     * - Chuyển driver status về ONLINE
     * 
     * @param orderId ID đơn hàng đã giao thành công (status = DELIVERED)
     * @return Success message
     */
    @PostMapping("/{orderId}/process-delivery-payment")
    public ResponseEntity<Map<String, String>> processDeliveryPayment(@PathVariable Long orderId) {
        log.info("[OrderController] Nhận yêu cầu xử lý thanh toán giao hàng: orderId={}", orderId);
        driverAssignmentService.processDeliveryPayment(orderId);
        log.info("[OrderController] Đã xử lý thanh toán giao hàng thành công: orderId={}", orderId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Payment processed successfully",
                "orderId", String.valueOf(orderId)
        ));
    }
}
