package com.example.qtifood.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.qtifood.enums.VerificationStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "drivers",
    indexes = {
        @Index(name = "idx_drivers_phone", columnList = "phone"),
        @Index(name = "idx_drivers_verification_status", columnList = "verification_status"),
        @Index(name = "idx_drivers_verified", columnList = "verified")
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Driver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(length = 20, unique = true, nullable = false)
    private String phone;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "vehicle_type", length = 50)
    private String vehicleType;

    @Column(name = "vehicle_plate", length = 20, unique = true)
    private String vehiclePlate;

    @Column(name = "cccd_number", length = 20)
    private String cccdNumber;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Builder.Default
    @Column(nullable = false)
    private Boolean verified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
