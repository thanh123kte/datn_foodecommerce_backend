package com.example.qtifood.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.qtifood.entities.Store;
import com.example.qtifood.entities.StoreStatus;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByOwnerId(Long ownerId);
    List<Store> findByStatus(StoreStatus status);
    List<Store> findByNameContainingIgnoreCase(String name);
}
