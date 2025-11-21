package com.example.qtifood.services;

import java.util.List;

import com.example.qtifood.dtos.Stores.*;
import com.example.qtifood.enums.StoreStatus;

public interface StoreService {
    StoreResponseDto createStore(CreateStoreDto dto);
    StoreResponseDto updateStore(Long id, UpdateStoreDto dto);
    void deleteStore(Long id);

    List<StoreResponseDto> getAllStores();
    List<StoreResponseDto> getStoresByOwner(String ownerId);
    StoreResponseDto getStoreById(Long id);
    List<StoreResponseDto> searchByName(String q);
    List<StoreResponseDto> getStoresByStatus(StoreStatus status);

    StoreResponseDto setStatus(Long id, StoreStatus status);
}
