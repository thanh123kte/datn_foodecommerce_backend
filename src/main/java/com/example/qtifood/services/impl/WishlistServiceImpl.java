package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Wishlists.CreateWishlistDto;
import com.example.qtifood.dtos.Wishlists.WishlistResponseDto;
import com.example.qtifood.entities.Store;
import com.example.qtifood.entities.User;
import com.example.qtifood.entities.Wishlist;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.exceptions.EntityDuplicateException;
import com.example.qtifood.mappers.WishlistMapper;
import com.example.qtifood.repositories.StoreRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.repositories.WishlistRepository;
import com.example.qtifood.services.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Override
    public WishlistResponseDto addToWishlist(Long customerId, CreateWishlistDto dto) {
        // Validate customer exists
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        // Validate store exists
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + dto.getStoreId()));

        // Check if store already exists in wishlist
        if (wishlistRepository.existsByCustomerIdAndStoreId(customerId, dto.getStoreId())) {
            throw new EntityDuplicateException("Store is already in wishlist");
        }

        // Create new wishlist item
        Wishlist wishlist = Wishlist.builder()
                .customer(customer)
                .store(store)
                .build();

        wishlist = wishlistRepository.save(wishlist);
        return WishlistMapper.toDto(wishlist);
    }

    @Override
    public void removeFromWishlist(Long customerId, Long storeId) {
        // Validate customer exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        // Validate store exists
        if (!storeRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("Store not found with id: " + storeId);
        }

        // Check if wishlist item exists
        if (!wishlistRepository.existsByCustomerIdAndStoreId(customerId, storeId)) {
            throw new ResourceNotFoundException("Store not found in wishlist");
        }

        wishlistRepository.deleteByCustomerIdAndStoreId(customerId, storeId);
    }

    @Override
    public void clearWishlist(Long customerId) {
        // Validate customer exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        wishlistRepository.deleteByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistResponseDto> getWishlist(Long customerId) {
        // Validate customer exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        List<Wishlist> wishlists = wishlistRepository.findByCustomerIdWithStoreDetails(customerId);
        return WishlistMapper.toDtoList(wishlists);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getWishlistCount(Long customerId) {
        // Validate customer exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        return wishlistRepository.countByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStoreInWishlist(Long customerId, Long storeId) {
        // Validate customer exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        return wishlistRepository.existsByCustomerIdAndStoreId(customerId, storeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getCustomersByStore(Long storeId) {
        // Validate store exists
        if (!storeRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("Store not found with id: " + storeId);
        }

        return wishlistRepository.findCustomerIdsByStoreId(storeId);
    }
}