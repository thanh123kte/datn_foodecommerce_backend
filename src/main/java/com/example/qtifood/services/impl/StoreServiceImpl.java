package com.example.qtifood.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.qtifood.dtos.Stores.*;
import com.example.qtifood.entities.Store;
import com.example.qtifood.entities.User;
import com.example.qtifood.enums.StoreStatus;
import com.example.qtifood.mappers.StoreMapper;
import com.example.qtifood.repositories.StoreRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.services.StoreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Override
    public StoreResponseDto createStore(CreateStoreDto dto) {
        User owner = userRepository.findById(dto.getOwnerId())
            .orElseThrow(() -> new RuntimeException("Owner not found: " + dto.getOwnerId()));

        Store s = Store.builder()
            .owner(owner)
            .name(dto.getName())
            .description(dto.getDescription())
            .address(dto.getAddress())
            .latitude(StoreMapper.toBD(dto.getLatitude()))
            .longitude(StoreMapper.toBD(dto.getLongitude()))
            .phone(dto.getPhone())
            .email(dto.getEmail())
            .imageUrl(dto.getImageUrl())
            .openTime(dto.getOpenTime())
            .closeTime(dto.getCloseTime())
            .status(StoreStatus.PENDING)
            .build();

        return StoreMapper.toDto(storeRepository.save(s));
    }

    @Override
    public StoreResponseDto updateStore(Long id, UpdateStoreDto dto) {
        Store s = storeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Store not found: " + id));

        if (dto.getName() != null)        s.setName(dto.getName());
        if (dto.getDescription() != null) s.setDescription(dto.getDescription());
        if (dto.getAddress() != null)     s.setAddress(dto.getAddress());
        if (dto.getLatitude() != null)    s.setLatitude(StoreMapper.toBD(dto.getLatitude()));
        if (dto.getLongitude() != null)   s.setLongitude(StoreMapper.toBD(dto.getLongitude()));
        if (dto.getPhone() != null)       s.setPhone(dto.getPhone());
        if (dto.getEmail() != null)       s.setEmail(dto.getEmail());
        if (dto.getImageUrl() != null)    s.setImageUrl(dto.getImageUrl());
        if (dto.getOpenTime() != null)    s.setOpenTime(dto.getOpenTime());
        if (dto.getCloseTime() != null)   s.setCloseTime(dto.getCloseTime());
        if (dto.getStatus() != null)      s.setStatus(dto.getStatus());

        return StoreMapper.toDto(storeRepository.save(s));
    }

    @Override
    public void deleteStore(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new RuntimeException("Store not found: " + id);
        }
        storeRepository.deleteById(id);
    }

    @Override @Transactional(readOnly = true)
    public List<StoreResponseDto> getAllStores() {
        return storeRepository.findAll().stream().map(StoreMapper::toDto).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<StoreResponseDto> getStoresByOwner(String ownerId) {
        return storeRepository.findByOwnerId(ownerId).stream().map(StoreMapper::toDto).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<StoreResponseDto> searchByName(String q) {
        return storeRepository.findByNameContainingIgnoreCase(q).stream().map(StoreMapper::toDto).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<StoreResponseDto> getStoresByStatus(StoreStatus status) {
        return storeRepository.findByStatus(status).stream().map(StoreMapper::toDto).toList();
    }

    @Override
    public StoreResponseDto setStatus(Long id, StoreStatus status) {
        Store s = storeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Store not found: " + id));
        s.setStatus(status);
        return StoreMapper.toDto(storeRepository.save(s));
    }
}
