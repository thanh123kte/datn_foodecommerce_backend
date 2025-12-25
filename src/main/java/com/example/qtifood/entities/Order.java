package com.example.qtifood.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.qtifood.enums.OrderStatus;
import com.example.qtifood.enums.PaymentMethod;
import com.example.qtifood.enums.PaymentStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id",
        foreignKey = @ForeignKey(
            name = "fk_order_customer",
            foreignKeyDefinition = "FOREIGN KEY (customer_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE"
        ))
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id",
        foreignKey = @ForeignKey(
            name = "fk_order_store",
            foreignKeyDefinition = "FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE"
        ))
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id",
        foreignKey = @ForeignKey(
            name = "fk_order_driver",
            foreignKeyDefinition = "FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE SET NULL"
        ))
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id",
        foreignKey = @ForeignKey(
            name = "fk_order_shipping_address",
            foreignKeyDefinition = "FOREIGN KEY (shipping_address_id) REFERENCES addresses(id) ON DELETE SET NULL"
        ))
    private Address shippingAddress;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "shipping_fee", precision = 12, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "admin_voucher_id")
    private Long adminVoucherId;

    @Column(name = "seller_voucher_id")
    private Long sellerVoucherId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    @Column(name = "expected_delivery_time")
    private LocalDateTime expectedDeliveryTime;

    @Column(name = "rating_status")
    private Boolean ratingStatus;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== ONE-TO-MANY CASCADE RELATIONSHIPS ==========
    
    // Order items in this order
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new HashSet<>();

    // Store reviews for this order
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<StoreReview> storeReviews = new HashSet<>();

    // Delivery for this order
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Delivery> deliveries = new HashSet<>();
}
