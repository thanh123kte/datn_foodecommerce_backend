package com.example.qtifood.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "deliveries")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "order_id",
        foreignKey = @ForeignKey(
            name = "fk_delivery_order",
            foreignKeyDefinition = "FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE"
        )
    )
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "driver_id",
        foreignKey = @ForeignKey(
            name = "fk_delivery_driver",
            foreignKeyDefinition = "FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE SET NULL"
        )
    )
    private Driver driver;

    @Column(name = "distance_km", precision = 6, scale = 2)
    private BigDecimal distanceKm;

    // Snapshot thông tin đơn để driver xem lịch sử
    @Column(name = "goods_amount", precision = 12, scale = 2)
    private BigDecimal goodsAmount;

    @Column(name = "shipping_fee", precision = 12, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "driver_income", precision = 12, scale = 2)
    private BigDecimal driverIncome;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private com.example.qtifood.enums.PaymentMethod paymentMethod;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "customer_name")
    private String customerName;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private com.example.qtifood.enums.DeliveryStatus status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}