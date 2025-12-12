package com.example.qtifood.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_tokens", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "token"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "user_id", insertable = false, updatable = false)
    private String userId;
    
    @Column(nullable = false)
    private String role;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;
    
    @Column(nullable = false)
    private String platform; // "iOS", "Android", "Web"
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
